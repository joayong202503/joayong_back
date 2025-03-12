package com.joayong.skillswap.service;

import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.rating.dto.request.RatingDetailRequest;
import com.joayong.skillswap.domain.rating.dto.request.RatingRequest;
import com.joayong.skillswap.domain.rating.entity.Rating;
import com.joayong.skillswap.domain.rating.entity.RatingDetail;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.PostException;
import com.joayong.skillswap.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    public double addRating(String email, RatingRequest dto) {

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new PostException(ErrorCode.USER_NOT_FOUND)
        );
        Post post = postRepository.findById(dto.getPostId()).orElse(null);
        User writer = post.getWriter();

        log.info("writer : {}",writer);

        Rating rating = Optional.ofNullable(writer.getRating())
                .orElseGet(()->{
                    Rating newRating = Rating.builder().user(writer).build();
                    writer.setRating(newRating);
                    ratingRepository.save(newRating);

                    log.info("newrating : {}",newRating);
                    return newRating;
                });
        log.info("rating : {}",rating);

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
                            .post(post)
                            .rating(rating)
                            .build();
                    ratingDetailRepository.save(ratingDetail);
                    newRating.addAndGet(ratingDto.getRating());
                    return ratingDetail;
                }).toList();

        double preTotal = rating.getTotalRating();

        log.info("preTotal: {}, newRating: {}, size + 5: {}", preTotal, newRating, size + 5);

        double total = ((preTotal* size) + newRating.get()) / (size + 5);

        log.info("total: {}", total);


        // 별점 평점 구하기
        rating.setTotalRating(total);
        ratingRepository.save(rating);

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
}
