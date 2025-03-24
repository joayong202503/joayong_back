package com.joayong.skillswap.domain.chat.dto.response;

import com.joayong.skillswap.domain.chat.entity.ChatMessage;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private Long chatRoomId;
    private String senderId;
    private String content;
    private LocalDateTime sentAt;

    public static ChatResponse of(ChatMessage chatMessage){
        return ChatResponse.builder()
                .chatRoomId(chatMessage.getChatRoomId())
                .senderId(chatMessage.getSenderId())
                .content(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .build();
    }
}
