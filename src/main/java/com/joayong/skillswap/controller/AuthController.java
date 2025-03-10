package com.joayong.skillswap.controller;

import com.joayong.skillswap.domain.user.dto.request.LoginRequest;
import com.joayong.skillswap.domain.user.dto.request.SignUpRequest;
import com.joayong.skillswap.domain.user.dto.response.DuplicateCheckResponse;
import com.joayong.skillswap.serivice.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/joayong/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // 회원가입 요청
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        log.info("request for signup: {}", signUpRequest.getEmail());
        userService.signUp(signUpRequest);

        return ResponseEntity
                .ok()
                .body(Map.of(
                        "message", "회원가입이 완료되었습니다.",
                        "username", signUpRequest.getEmail()
                ));
    }

    // 중복확인을 검사하는 API
    @GetMapping("/email-duplicate")
    public ResponseEntity<DuplicateCheckResponse> checkDuplicate(
            @RequestParam String email
    ) {
        log.info("check duplicate email: {}", email);

        DuplicateCheckResponse responseDto = userService.checkDuplicate(email);

        return ResponseEntity.ok().body(responseDto);
    }

    // 로그인 검증 API
    // GET방식의 특징 : ? 를 사용할 수 있음 => 보안상 좋지않음
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid LoginRequest loginRequest
            , HttpServletResponse response
    ) {

        log.info("request for authentication user : {}", loginRequest.getEmail());

        Map<String, Object> responseMap = userService.authenticate(loginRequest);

        /*
         로그인이 성공하면 클라이언트에게 2가지 인증정보를 전달해야 한다.

         첫번째는 API요청을 위한 토큰정보를 JSON에 담아 전달하고
         두번째는 페이지 라우팅 요청을 위한 쿠키를 구워서 전달해야 함.
         */
        Cookie cookie = new Cookie("accessToken", (String) responseMap.get("accessToken"));
        // 쿠키의 수명, 사용경로, 보안 등을 설정
        cookie.setMaxAge(60 * 60); // 단위: 초
        cookie.setPath("/");
        cookie.setHttpOnly(true); // 보안설정 - 자바스크립트로는 쿠키에 접근 불가

        // 쿠키를 클라이언트에 전송
        response.addCookie(cookie);

        return ResponseEntity.ok().body(responseMap);
    }

    // 로그아웃 처리 API
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        // 쿠키 무효화
        Cookie cookie = new Cookie("accessToken", null);
        // 쿠키의 수명, 사용경로, 보안 등을 설정
        cookie.setMaxAge(0); // 단위: 초
        cookie.setPath("/");
        cookie.setHttpOnly(true); // 보안설정 - 자바스크립트로는 쿠키에 접근 불가

        // 쿠키를 클라이언트에 전송
        response.addCookie(cookie);

        return ResponseEntity.ok().body(Map.of(
                "message", "로그아웃이 처리되었습니다."
        ));
    }

}