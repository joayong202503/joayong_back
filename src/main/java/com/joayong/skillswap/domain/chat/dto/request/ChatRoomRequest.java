package com.joayong.skillswap.domain.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRequest {
    private String user1Name;
    private String user2Name;
}