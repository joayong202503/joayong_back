package com.joayong.skillswap.service;

import com.joayong.skillswap.domain.rating.dto.response.RatingDetailResponse;
import com.joayong.skillswap.domain.rating.dto.response.RatingResponse;
import com.joayong.skillswap.domain.rating.dto.response.ReviewResponse;
import com.joayong.skillswap.domain.rating.entity.Rating;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.dto.common.PageResponse;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.PostException;
import com.joayong.skillswap.repository.*;
import com.querydsl.core.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RatingServiceTest {

    @Autowired
    private RatingDetailRepository ratingDetailRepository;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private ReviewItemRepository reviewItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MessageRepository messageRepository;

    @Test
    @DisplayName("")
    void getPagingRatingListTest() {
        //given
        String username = "qqq";
        Pageable pageable = PageRequest.of(0,1);

        //when
        User user = userRepository.findByName(username).orElseThrow(
                () -> new PostException(ErrorCode.USER_NOT_FOUND)
        );
        Rating rating = user.getRating();

        // 리뷰 얻은거 없으면 빈값보냄
        if (rating == null) {
            System.out.println("rating = " + rating);
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
//                        ratingDetail.createdAt      //8

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
                        .ratingDetailId(tuple.get(1, String.class))
                        .postId(tuple.get(5, String.class))
                        .messageId(tuple.get(6, String.class))
                        .reviewer(tuple.get(7, String.class))
                        .createAt(tuple.get(8, LocalDateTime.class))
                        .reviewList(new ArrayList<>())
                        .build();

                detailResponse.getReviewList().add(reviewResponse);
                ratingMap.put(messageId, detailResponse);
            }
        }

        RatingResponse response = RatingResponse.builder()
                .ratingList(new ArrayList<>(ratingMap.values()))
                .totalRating(totalRating)
                .build();

        // 페이지네이션 계산
        int totalPages = (int) Math.ceil((double) totalCount / pageable.getPageSize());
        boolean hasNext = pageable.getPageNumber() < totalPages - 1;
        boolean hasPrevious = pageable.getPageNumber() > 0;

        PageResponse<Object> pageResponse = PageResponse.builder()
                .totalCount(totalCount)
                .totalPages(totalPages)
                .currentPage(pageable.getPageNumber()+1) // 클라이언트에선 1페이지가 첫페이지니까 더해줌
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .pageSize(pageable.getPageSize())
                .data(List.of(response))
                .build();

        System.out.println("pageResponse = " + pageResponse);
    }
}