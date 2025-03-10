package com.joayong.skillswap.controller;

import com.joayong.skillswap.domain.user.dto.response.UserProfileResponse;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.serivice.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok().body(response);
    }


}
