package com.joayong.skillswap.domain.message.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class MessageUrlResponse {

    private String id;

    private int sequence;

    private String imageUrl;
}
