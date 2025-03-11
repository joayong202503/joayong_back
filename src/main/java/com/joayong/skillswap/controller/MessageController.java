package com.joayong.skillswap.controller;

import com.joayong.skillswap.domain.message.dto.request.MessageRequest;
import com.joayong.skillswap.serivice.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<?> getCategoryList(
            @RequestPart(value = "message", required = true) MessageRequest dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal String email
    ) {

        String messageId = messageService.sendMessage(email, dto, images);
        return ResponseEntity.ok().body(Map.of
                ("message","메세지가 전송되었습니다.",
                "messageId",messageId)
        );
    }
}
