package com.joayong.skillswap.domain.rating.dto.request;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class RatingDetailRequest {
    private Long index;
    private int rating;
}
