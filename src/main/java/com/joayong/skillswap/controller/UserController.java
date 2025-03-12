package com.joayong.skillswap.controller;

import com.joayong.skillswap.domain.user.dto.response.UserProfileResponse;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.UserException;
import com.joayong.skillswap.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/joayong/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/me")
    public ResponseEntity<?> getCurrentUser(
        @AuthenticationPrincipal String email
    ){
        User founduser = userService.findMe(email);
        return ResponseEntity.ok().body(Map.of(
                "message","인증되었습니다",
                "id",founduser.getId(),
                "email",founduser.getEmail(),
                "name",founduser.getName(),
                "created_at",founduser.getCreatedAt()
        ));
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable String id
    ){
        UserProfileResponse response = userService.findUserProfile(id);
        log.info("response : "+response);
        if(response==null){
            throw new UserException(ErrorCode.USER_NOT_FOUND);
        }
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/update/name")
    public ResponseEntity<?> updateName(
            @AuthenticationPrincipal String email,
            @RequestParam String newname
    ){
        userService.updateName(email,newname);
        return ResponseEntity.ok().body(Map.of(
                "message","닉네임 변경 완료",
                "name",newname
        ));
    }

    @PutMapping("/update/profile-image")
    public ResponseEntity<?> updateProfileImage(
            @AuthenticationPrincipal String email,
            @RequestParam("profile-image") MultipartFile profileImage
            ){
        String imageUrl = userService.updateProfileImage(email, profileImage);
        return ResponseEntity.ok().body(Map.of(
                "imageUrl", imageUrl,
                "message", "프로필 이미지 업로드 성공"
        ));
    }

}
