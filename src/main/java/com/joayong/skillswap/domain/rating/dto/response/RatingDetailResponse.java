package com.joayong.skillswap.domain.rating.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class RatingDetailResponse {
    private String ratingDetailId;
    private String postId;
    private String messageId;
    private String reviewer;
    private String reviewerProfileUrl;
    private LocalDateTime createAt;
    private List<ReviewResponse> reviewList;
}
