package com.joayong.skillswap.domain.image.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class PostImageUrlResponse {
    private String imageUrl;
    private String id;
    private int sequence;
}
