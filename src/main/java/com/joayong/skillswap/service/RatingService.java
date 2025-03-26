package com.joayong.skillswap.service;

import com.joayong.skillswap.domain.message.dto.response.MessageResponse;
import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.rating.dto.request.RatingDetailRequest;
import com.joayong.skillswap.domain.rating.dto.request.RatingRequest;
import com.joayong.skillswap.domain.rating.dto.response.RatingDetailResponse;
import com.joayong.skillswap.domain.rating.dto.response.RatingResponse;
import com.joayong.skillswap.domain.rating.dto.response.ReviewResponse;
import com.joayong.skillswap.domain.rating.entity.Rating;
import com.joayong.skillswap.domain.rating.entity.RatingDetail;
import com.joayong.skillswap.domain.room.entity.RtcRoom;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.dto.common.PageResponse;
import com.joayong.skillswap.enums.MessageStatus;
import com.joayong.skillswap.enums.PostStatus;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.PostException;
import com.joayong.skillswap.repository.*;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RatingDetailRepository ratingDetailRepository;
    private final ReviewItemRepository reviewItemRepository;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MessageRepository messageRepository;

    private final RtcRoomRepository rtcRoomRepository;

    public double addRating(String email, RatingRequest dto) {

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new PostException(ErrorCode.USER_NOT_FOUND)
        );

        Message message = messageRepository.findById(dto.getMessageId()).orElseThrow(
                () -> new PostException(ErrorCode.NOT_FOUND_MESSAGE)
        );

        Post post = message.getPost();
        User writer = post.getWriter();

        log.info("writer : {}", writer);

        Rating rating = Optional.ofNullable(writer.getRating())
                .orElseGet(() -> {
                    Rating newRating = Rating.builder().user(writer).build();
                    writer.setRating(newRating);
                    ratingRepository.save(newRating);

                    log.info("newrating : {}", newRating);
                    return newRating;
                });
        log.info("rating : {}", rating);

        int size = rating.getRatingDetails().size();

        log.info(" rating:{}", rating);

        List<RatingDetailRequest> ratingList = dto.getRatingDetailtList();

        AtomicInteger newRating = new AtomicInteger(0);

        List<RatingDetail> ratingDetailList
                = ratingList.stream()
                .map(ratingDto -> {
                    RatingDetail ratingDetail = RatingDetail.builder()
                            .ratingValue(ratingDto.getRating())
                            .reviewItem(reviewItemRepository.findById(ratingDto.getIndex()).orElseThrow(
                                    () -> new PostException(ErrorCode.INVALID_REVIEW_INDEX)
                            ))
                            .user(user)
                            .message(message)
                            .post(post)
                            .rating(rating)
                            .build();
                    ratingDetailRepository.save(ratingDetail);
                    newRating.addAndGet(ratingDto.getRating());
                    return ratingDetail;
                }).toList();

        double preTotal = rating.getTotalRating();

        double total = ((preTotal * size) + newRating.get()) / (size + ratingDetailList.size());

        log.info("total: {}", total);

        // 별점 평점 구하기
        rating.setTotalRating(total);
        ratingRepository.save(rating);

        message.setMsgStatus(MessageStatus.C);
        messageRepository.save(message);

        // rtc 방 빼기 (강의 완료 후 방 빼고 있지만 나중엔 해당 방 사용안하면 없앨 예정)
        RtcRoom rtcRoom = rtcRoomRepository.findByIsAvailable(message.getId()).orElse(null);

        if(rtcRoom != null){
            rtcRoom.setIsAvailable(null);
            rtcRoomRepository.save(rtcRoom);
        }

        log.info("rating : {}", rating);
        log.info("ratingDetailList : {}", ratingDetailList);

        return total;

    }

    private double getCalculateRating(Rating rating) {
        List<RatingDetail> detailList = ratingDetailRepository.findByRating(rating);

        double total = detailList.stream().map(RatingDetail::getRatingValue)
                .mapToInt(Integer::intValue)
                .average().orElse(0.0);

        rating.setTotalRating(total);
        ratingRepository.save(rating);

        return total;
    }

    public RatingResponse getRatingList(String username) {

        User user = userRepository.findByName(username).orElseThrow(
                () -> new PostException(ErrorCode.USER_NOT_FOUND)
        );
        Rating rating = user.getRating();

        if (rating == null) {
            return RatingResponse.builder().build();
        }

        List<String> messageIds = ratingDetailRepository.findDistinctMessageIds();
        List<RatingDetail> ratingDetails = ratingDetailRepository.findAll();

        Map<String, Set<Long>> addedReviewItemsByMessage = new HashMap<>();
        Map<String, List<ReviewResponse>> groupedReviews = new HashMap<>();
        Map<String, User> reviewerByMessage = new HashMap<>(); // postId별 reviewer 저장

        for (RatingDetail ratingDetail : ratingDetails) {
            String messageId = ratingDetail.getMessage().getId();
            Long reviewItemId = ratingDetail.getReviewItem().getId();

            // 리뷰어 저장 (같은 postId에 여러 명이 있을 수도 있지만, 하나만 저장)
            reviewerByMessage.putIfAbsent(messageId, ratingDetail.getUser());

            // postId별로 이미 추가된 reviewItemId를 체크하기 위해 Set 사용
            addedReviewItemsByMessage.putIfAbsent(messageId, new HashSet<>());
            groupedReviews.putIfAbsent(messageId, new ArrayList<>());

            // 같은 reviewItemId가 추가되지 않았으면 리스트에 추가
            if (addedReviewItemsByMessage.get(messageId).add(reviewItemId)) {
                groupedReviews.get(messageId).add(ReviewResponse.builder()
                        .index(reviewItemId)
                        .question(ratingDetail.getReviewItem().getQuestion())
                        .rating(ratingDetail.getRatingValue())
                        .build());
            }
        }

        List<RatingDetailResponse> ratingDetailResponseList = messageIds.stream()
                .map(messageId -> {
                    Message message = messageRepository.findById(messageId).orElse(null);
                    return RatingDetailResponse.builder()
                            .ratingDetailId(reviewerByMessage.get(messageId).getId())
                            .postId(message.getPost().getId())
                            .messageId(messageId)
                            .createAt(reviewerByMessage.get(messageId).getCreatedAt())
                            .reviewerProfileUrl(reviewerByMessage.get(messageId).getProfileUrl())
                            .reviewer(reviewerByMessage.get(messageId).getName()) // reviewer 추가
                            .reviewList(groupedReviews.getOrDefault(messageId, List.of())
                                    .stream()
                                    .sorted(Comparator.comparing(ReviewResponse::getIndex)) // index 기준 정렬
                                    .toList())
                            .build();

                }).toList();


        return RatingResponse.builder()
                .totalRating(rating.getTotalRating())
                .ratingList(ratingDetailResponseList)
                .build();
    }

    // 페이징 처리되는 리뷰 조회 서비스
    public PageResponse<RatingResponse> getPagingRatingList(String username, Pageable pageable) {

        User user = userRepository.findByName(username).orElseThrow(
                () -> new PostException(ErrorCode.USER_NOT_FOUND)
        );
        Rating rating = user.getRating();

        // 리뷰 얻은거 없으면 빈값보냄
        if (rating == null) {
            return PageResponse.<RatingResponse>builder()
                    .totalCount(0)
                    .totalPages(0)
                    .currentPage(pageable.getPageNumber() + 1) // 클라이언트에선 1페이지가 첫페이지니까 더해줌
                    .hasNext(false)
                    .hasPrevious(false)
                    .pageSize(pageable.getPageSize())
                    .data(Collections.emptyList()) // 빈 리스트 반환
                    .build();
        }

        // 전체 데이터 개수 조회
        long totalCount = ratingDetailRepository.countDistinctMessageIdsByRatingId(rating.getId());

        List<Tuple> ratingTupleList = ratingRepository.getRatingList(rating.getId(), pageable);

        // 전체 평점 추출
        Double totalRating = ratingTupleList.get(0).get(0, Double.class);

        Map<String, RatingDetailResponse> ratingMap = new LinkedHashMap<>();

        for (Tuple tuple : ratingTupleList) {

//                        rating.totalRating,         //0
//                        ratingDetail.id,            //1
//                        ratingDetail.reviewItem,    //2
//                        reviewItem.question,        //3
//                        ratingDetail.ratingValue,   //4
//                        ratingDetail.post.id,       //5
//                        ratingDetail.message.id,    //6
//                        user.name,                  //7
//                        user.profileUrl             //8
//                        ratingDetail.createdAt      //9

            String messageId = tuple.get(6, String.class);

            ReviewResponse reviewResponse = ReviewResponse.builder()
                    .index(tuple.get(2, Long.class))
                    .question(tuple.get(3, String.class))
                    .rating(tuple.get(4, Integer.class))
                    .build();

            // 메세지 기준으로 map에 저장, 이미 있는 경우
            if (ratingMap.containsKey(messageId)) {
                ratingMap.get(messageId).getReviewList().add(reviewResponse);

            } else {
                //없는 경우 새로 put
                RatingDetailResponse detailResponse = RatingDetailResponse.builder()
                        .ratingDetailId(tuple.get(1,String.class))
                        .postId(tuple.get(5, String.class))
                        .messageId(tuple.get(6, String.class))
                        .reviewer(tuple.get(7, String.class))
                        .reviewerProfileUrl(tuple.get(8,String.class))
                        .createAt(tuple.get(9, LocalDateTime.class))
                        .reviewList(new ArrayList<>())
                        .build();

                detailResponse.getReviewList().add(reviewResponse);
                ratingMap.put(messageId, detailResponse);
            }
        }

        RatingResponse ratingResponseList = RatingResponse.builder()
                .ratingList(new ArrayList<>(ratingMap.values()))
                .totalRating(totalRating)
                .build();

        // 페이지네이션 계산
        int totalPages = (int) Math.ceil((double) totalCount / pageable.getPageSize());
        boolean hasNext = pageable.getPageNumber() < totalPages - 1;
        boolean hasPrevious = pageable.getPageNumber() > 0;

        // 제너릭 타입 T를 RatingResponse 지정하고, build() 메서드를 호출하여 객체를 반환
        return PageResponse.<RatingResponse>builder()
                .totalCount(totalCount)
                .totalPages(totalPages)
                .currentPage(pageable.getPageNumber() + 1) // 클라이언트에선 1페이지가 첫페이지니까 더해줌
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .pageSize(pageable.getPageSize())
                .data(List.of(ratingResponseList))
                .build();
    }
}
