package com.joayong.skillswap.controller;

import com.joayong.skillswap.domain.chat.dto.request.ChatRequest;
import com.joayong.skillswap.domain.chat.dto.request.EnteredChatRequest;
import com.joayong.skillswap.domain.chat.dto.response.ChatResponse;
import com.joayong.skillswap.domain.chat.entity.ChatMessage;
import com.joayong.skillswap.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocket {
    private final ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat.send/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatResponse sendMessage(
            @DestinationVariable Long roomId,
            @Payload ChatRequest request
    ) {
        ChatMessage message = new ChatMessage();
        message.setChatRoomId(roomId);
        message.setSenderId(request.getSenderId());
        message.setContent(request.getContent());
        message.setSentAt(LocalDateTime.now());
        chatMessageRepository.save(message);

        log.info("receive message : {}", message);
        return ChatResponse.of(message);
    }

    @MessageMapping("/chat.enter/{roomId}")  // 클라이언트가 입장할 때 요청 보냄
    @SendTo("/topic/chat/{roomId}")  // 해당 방의 모든 참가자에게 전송
    public EnteredChatRequest enterChat(
            @DestinationVariable Long roomId,
            @Payload EnteredChatRequest message) {
        message.setContent(message.getSenderName() + "님이 입장하셨습니다 😊");
        message.setType("H");
        log.info("entered user : {}", message.getSenderName());
        return message; // 클라이언트들에게 자동으로 전송됨
    }

}
