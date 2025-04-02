package com.joayong.skillswap.domain.rating.dto.request;

import com.joayong.skillswap.domain.rating.entity.RatingDetail;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class RatingRequest {
    private String messageId;
    private List<RatingDetailRequest> ratingDetailtList;
}
