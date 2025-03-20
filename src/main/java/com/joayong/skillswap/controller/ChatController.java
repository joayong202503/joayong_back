package com.joayong.skillswap.controller;

import com.joayong.skillswap.domain.chat.dto.request.ChatRoomRequest;
import com.joayong.skillswap.domain.chat.dto.response.ChatResponse;
import com.joayong.skillswap.domain.chat.dto.response.ChatRoomResponse;
import com.joayong.skillswap.domain.chat.entity.ChatMessage;
import com.joayong.skillswap.domain.chat.entity.ChatRoom;
import com.joayong.skillswap.repository.ChatMessageRepository;
import com.joayong.skillswap.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChatController {
    private ChatMessageRepository chatMessageRepository;
    private ChatService chatService;

    @PostMapping("/chatroom")
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @RequestBody ChatRoomRequest request
    ) {
        ChatRoom chatRoom = chatService.createOrGetChatRoom(request.getUser1Id(), request.getUser2Id());
        return ResponseEntity.ok(new ChatRoomResponse(chatRoom.getId(), chatRoom.getUser1Id(), chatRoom.getUser2Id()));
    }

    @MessageMapping("/chat.send/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable Long roomId,
                                   @Payload ChatMessage message,
                                   Principal principal) {
        // 메시지 저장
        message.setChatRoomId(roomId);
        message.setSenderId(principal.getName()); // 로그인된 유저 ID
        message.setSentAt(LocalDateTime.now());
        chatMessageRepository.save(message);

        return message;
    }

    @GetMapping("/chatrooms/{roomId}/history")
    public ResponseEntity<List<ChatResponse>> getChatHistory(
            @PathVariable Long roomId
    ) {
        List<ChatResponse> history = chatService.getChatHistory(roomId);
        return ResponseEntity.ok(history);
    }
}
