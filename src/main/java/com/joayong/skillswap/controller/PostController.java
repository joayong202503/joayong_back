package com.joayong.skillswap.controller;

import com.joayong.skillswap.domain.post.dto.request.PostCreate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/joayong/post")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    @PostMapping
    public ResponseEntity<?> createPost(
            @AuthenticationPrincipal String email,
            // 피드 내용, 작성자 이름 JSON { "writer": "", "content": "" } -> 검증
            @RequestPart("feed") @Valid PostCreate postCreate,
            @RequestPart("images") List<MultipartFile> images
    ){
        return null;
    }
}
