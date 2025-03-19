package com.joayong.skillswap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatController {
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private ChatRoomService chatRoomService;

    @MessageMapping("/chat.send/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable Long roomId,
                                   @Payload ChatMessage message,
                                   Principal principal) {
        // 메시지 저장
        message.setChatRoomId(roomId);
        message.setSender(principal.getName()); // 로그인된 유저 ID
        message.setSentAt(LocalDateTime.now());
        chatMessageRepository.save(message);

        return message;
    }
}
