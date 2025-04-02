package com.joayong.skillswap.controller;

import com.joayong.skillswap.domain.message.dto.request.MessageRequest;
import com.joayong.skillswap.domain.message.dto.response.MessageDetailResponse;
import com.joayong.skillswap.domain.message.dto.response.MessageResponse;
import com.joayong.skillswap.domain.post.dto.response.PostSampleResponse;
import com.joayong.skillswap.dto.common.PageResponse;
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
    public ResponseEntity<Map<String,Object>> sendMessage(
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
    public ResponseEntity<List<MessageResponse>> getMessageList(
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

    @PutMapping("/accept/{messageId}")
    public ResponseEntity<Map<String,Boolean>> acceptMessage(
            @PathVariable String messageId,
            @AuthenticationPrincipal String email
    ){
        boolean isAccept = messageService.acceptMessage(messageId, email);
        return ResponseEntity.ok().body(
                Map.of("isAccept", isAccept)
        );
    }

    @PutMapping("/reject/{messageId}")
    public ResponseEntity<Map<String,Boolean>> rejectMessage(
            @PathVariable String messageId,
            @AuthenticationPrincipal String email
    ){
        boolean isReject = messageService.rejectMessage(messageId, email);
        return ResponseEntity.ok().body(
                Map.of("isReject", isReject)
        );
    }

    @PutMapping("/complete/{messageId}")
    public ResponseEntity<Map<String,Boolean>>completeMessage(
            @PathVariable String messageId,
            @AuthenticationPrincipal String email
    ){
        boolean isCompleted = messageService.completeMessage(messageId, email);
        return ResponseEntity.ok().body(
                Map.of("isCompleted", isCompleted)
        );
    }

    @GetMapping("/paging")
    public ResponseEntity<PageResponse<MessageResponse>> getPagingMessage(
            @RequestParam String filter,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @AuthenticationPrincipal String email
    ){
        Pageable pageable = PageRequest.of(page,size);
        PageResponse<MessageResponse> pagingMessage = messageService.findPagingMessage(email, filter, status, pageable);

        return ResponseEntity.ok().body(pagingMessage);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<?> getMessageUrlList(
            @PathVariable String messageId
    ){
        MessageDetailResponse messageUrlList = messageService.getMessageUrlList(messageId);

        return ResponseEntity.ok().body(messageUrlList);
    }

    @GetMapping("/postInfo/{messageId}")
    public ResponseEntity<?> getPostInfo(
            @PathVariable String messageId
    ){
        PostSampleResponse postInfo = messageService.getPostInfo(messageId);

        return ResponseEntity.ok().body(postInfo);
    }
}
