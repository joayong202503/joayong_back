package com.joayong.skillswap.service;

import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.rating.dto.request.RatingDetailRequest;
import com.joayong.skillswap.domain.rating.dto.request.RatingRequest;
import com.joayong.skillswap.domain.rating.dto.response.RatingDetailResponse;
import com.joayong.skillswap.domain.rating.dto.response.RatingResponse;
import com.joayong.skillswap.domain.rating.dto.response.ReviewResponse;
import com.joayong.skillswap.domain.rating.entity.Rating;
import com.joayong.skillswap.domain.rating.entity.RatingDetail;
import com.joayong.skillswap.domain.rating.entity.ReviewItem;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.enums.PostStatus;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.PostException;
import com.joayong.skillswap.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

        message.setMsgStatus(PostStatus.C);
        messageRepository.save(message);

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
                            .postId(message.getPost().getId())
                            .messageId(messageId)
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
}
