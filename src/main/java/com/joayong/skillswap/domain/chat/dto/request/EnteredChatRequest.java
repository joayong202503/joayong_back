package com.joayong.skillswap.domain.chat.dto.request;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class EnteredChatRequest {
    private String roomId;
    private String senderName;
    private String content;
    private String type;
}
