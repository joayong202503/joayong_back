package com.joayong.skillswap.domain.message.dto.request;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class MessageRequest {

    private String postId;

    private String content;

}
