package com.joayong.skillswap.controller;

import com.joayong.skillswap.domain.chat.dto.request.ChatRoomRequest;
import com.joayong.skillswap.domain.chat.dto.response.ChatResponse;
import com.joayong.skillswap.domain.chat.dto.response.ChatRoomResponse;
import com.joayong.skillswap.domain.chat.entity.ChatMessage;
import com.joayong.skillswap.domain.chat.entity.ChatRoom;
import com.joayong.skillswap.repository.ChatMessageRepository;
import com.joayong.skillswap.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/joayong/chat")
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatService chatService;

    @PostMapping("/chatroom")
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @RequestBody ChatRoomRequest request
    ) {
        ChatRoom chatRoom = chatService.createOrGetChatRoom(request.getUser1Name(), request.getUser2Name());
        return ResponseEntity.ok(new ChatRoomResponse(chatRoom.getId(), chatRoom.getUser1Id(), chatRoom.getUser2Id()));
    }

    @GetMapping("/chatroom/{roomId}/history")
    public ResponseEntity<List<ChatResponse>> getChatHistory(
            @PathVariable Long roomId
    ) {
        List<ChatResponse> history = chatService.getChatHistory(roomId);
        return ResponseEntity.ok(history);
    }
}
