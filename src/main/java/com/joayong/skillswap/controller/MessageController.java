package com.joayong.skillswap.controller;

import com.joayong.skillswap.domain.message.dto.request.MessageRequest;
import com.joayong.skillswap.domain.message.dto.response.MessageResponse;
import com.joayong.skillswap.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/joayong/message")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<?> sendMessage(
            @RequestPart(value = "message", required = true) MessageRequest dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal String email
    ) {

        String messageId = messageService.sendMessage(email, dto, images);
        return ResponseEntity.ok().body(Map.of
                ("message", "메세지가 전송되었습니다.",
                        "messageId", messageId)
        );
    }

    @GetMapping
    public ResponseEntity<?> getMessageList(
            @RequestParam(name = "filter") String filter,
            @RequestParam(name = "status", required = false) String status,
            @AuthenticationPrincipal String email
    ) {
        filter = filter.toUpperCase();
        if (status != null) {
            status = status.toUpperCase();
        }

        List<MessageResponse> messageResponseList
                = messageService.findMessages(email, filter, status);

        return ResponseEntity.ok().body(messageResponseList);
    }

    @PostMapping("/available")
    public ResponseEntity<Map<String, Boolean>> isMessageSendable(
            @RequestParam String postId,
            @AuthenticationPrincipal String email
    ) {
        boolean isAvailable = messageService.canSendMessage(postId, email);
        return ResponseEntity.ok().body(
                Map.of("available", isAvailable)
        );
    }

    @PutMapping("/accept")
    public ResponseEntity<?> acceptMessage(
            @RequestParam String messageId,
            @AuthenticationPrincipal String email
    ){
        boolean isAccept = messageService.acceptMessage(messageId, email);
        return ResponseEntity.ok().body(
                Map.of("isAccept", isAccept)
        );
    }

    @PutMapping("/reject")
    public ResponseEntity<?> rejectMessage(
            @RequestParam String messageId,
            @AuthenticationPrincipal String email
    ){
        boolean isReject = messageService.rejectMessage(messageId, email);
        return ResponseEntity.ok().body(
                Map.of("isReject", isReject)
        );
    }

    @GetMapping("/paging")
    public ResponseEntity<?> getPagingMessage(
            @RequestParam String filter,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @AuthenticationPrincipal String email
    ){
        Pageable pageable = PageRequest.of(page,size);
        Page<MessageResponse> pagingMessage = messageService.findPagingMessage(email, filter, status, pageable);

        return ResponseEntity.ok().body(pagingMessage);
    }
}
