package com.joayong.skillswap.domain.rating.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ReviewResponse {
    private Long index;
    private String question;
    private int rating;
}
