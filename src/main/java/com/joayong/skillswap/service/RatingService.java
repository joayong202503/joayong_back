package com.joayong.skillswap.service;

import com.joayong.skillswap.domain.rating.dto.request.RatingDetailRequest;
import com.joayong.skillswap.domain.rating.dto.request.RatingRequest;
import com.joayong.skillswap.domain.rating.entity.Rating;
import com.joayong.skillswap.domain.rating.entity.RatingDetail;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.PostException;
import com.joayong.skillswap.repository.RatingDetailRepository;
import com.joayong.skillswap.repository.RatingRepository;
import com.joayong.skillswap.repository.ReviewItemRepository;
import com.joayong.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public void addRating(String email, RatingRequest dto) {

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new PostException(ErrorCode.USER_NOT_FOUND)
        );
        Rating rating = user.getRating();

        if (rating == null) {
            Rating newRating = Rating.builder().build();
            ratingRepository.save(newRating);
            user.setRating(newRating);
            userRepository.save(user);
        }

        List<RatingDetailRequest> ratingList = dto.getRatingDetailtList();

        double totalRating = rating.getTotalRating();

        List<RatingDetail> ratingDetailList
                = ratingList.stream()
                .map(ratingDto -> {
                    RatingDetail ratingDetail = RatingDetail.builder()
                            .ratingValue(ratingDto.getRating())
                            .reviewItem(reviewItemRepository.findById(ratingDto.getIndex()).orElseThrow(
                                    () -> new PostException(ErrorCode.INVALID_REVIEW_INDEX)
                            ))
                            .rating(rating)
                            .build();
                    ratingDetailRepository.save(ratingDetail);
                    return ratingDetail;
                }).toList();

        rating.setRatingDetails(ratingDetailList);
        ratingRepository.save(rating);

        // 별점 평점 구하는 메서드
        double total = getCalculateRating(rating);

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
