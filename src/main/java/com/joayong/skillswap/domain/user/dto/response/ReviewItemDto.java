package com.joayong.skillswap.domain.user.dto.response;

import com.joayong.skillswap.domain.rating.entity.ReviewItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewItemDto {
    private Long id;
    private String question;

    public static ReviewItemDto of(ReviewItem reviewItem) {
        return ReviewItemDto.builder()
                .id(reviewItem.getId())
                .question(reviewItem.getQuestion())
                .build();
    }
}