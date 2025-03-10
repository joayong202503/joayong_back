package com.joayong.skillswap.domain.user.dto.response;

import com.joayong.skillswap.domain.rating.entity.RatingDetail;
import com.joayong.skillswap.domain.rating.entity.ReviewItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingDetailDto {
    private String id;
    private int ratingValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReviewItemDto> reviewItems;

    public static RatingDetailDto of(RatingDetail ratingDetail) {
        return RatingDetailDto.builder()
                .id(ratingDetail.getId())
                .ratingValue(ratingDetail.getRatingValue())
                .createdAt(ratingDetail.getCreatedAt())
                .updatedAt(ratingDetail.getUpdatedAt())
                .reviewItems(
                        ratingDetail.getReviewItems().stream()
                                .map(ReviewItemDto::of)
                                .toList()
                )
                .build();
    }
}
