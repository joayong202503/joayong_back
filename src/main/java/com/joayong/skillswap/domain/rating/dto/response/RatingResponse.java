package com.joayong.skillswap.domain.rating.dto.response;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class RatingResponse {
    private double totalRating;
    private List<RatingDetailResponse> ratingList;
}
