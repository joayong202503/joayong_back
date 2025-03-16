package com.joayong.skillswap.controller;

import com.joayong.skillswap.domain.post.dto.request.PostCreateRequest;
import com.joayong.skillswap.domain.post.dto.request.PostCreateRequest;
import com.joayong.skillswap.domain.post.dto.request.UpdatePostRequest;
import com.joayong.skillswap.domain.post.dto.response.PostResponse;
import com.joayong.skillswap.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/joayong/post")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    //게시글 등록
    @PostMapping
    public ResponseEntity<?> createPost(
            @AuthenticationPrincipal String email,
            @RequestPart("post") PostCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ){
        postService.createPost(email,request,images);
        return ResponseEntity.ok().body(Map.of(
                "message","등록이 완료되었습니다."
        ));
    }

    //게시글 수정
    @PutMapping("/update")
    public ResponseEntity<?> updatePost(
            @AuthenticationPrincipal String email,
            @RequestPart("post") UpdatePostRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ){
        postService.updatePost(email,request,images);
        return ResponseEntity.ok().body(Map.of(
                "message","수정완료"
        ));
    }


    //전체 게시글 조회
    @GetMapping("/main")
    public ResponseEntity<?> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ){
        Pageable pageable = PageRequest.of(page,size);

        return ResponseEntity.ok().body(postService.findPosts(pageable));
    }

    //단일 게시글 조회
    @GetMapping("/{exchangeId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable String exchangeId
    ){
        return ResponseEntity.ok().body(postService.findPostById(exchangeId));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePost(
            @AuthenticationPrincipal String email,
            @RequestParam("id") String postId
    ){
        postService.deletePost(postId,email);
        return ResponseEntity.ok().body(Map.of(
                "message","삭제완료"
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyPosts(
            @AuthenticationPrincipal String email
    ){
        return ResponseEntity.ok().body(postService.findMyPosts(email));
    }

    //특정 유저 포스트 전부
    @GetMapping("user")
    public ResponseEntity<?> getUserPosts(
        @RequestParam("id") String userId
    ){
        return ResponseEntity.ok().body(postService.findUserPosts(userId));
    }

    @PostMapping("/view-count")
    public ResponseEntity<?> viewCount(
            @Nullable @AuthenticationPrincipal String email,
            @RequestParam("id") String postId
    ){
        postService.viewCount(postId,email);
        return ResponseEntity.ok().body(Map.of(
                "message","조회수 증가"
        ));
    }


}
