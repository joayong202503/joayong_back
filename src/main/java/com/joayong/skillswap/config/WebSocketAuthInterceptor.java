package com.joayong.skillswap.config;

import com.joayong.skillswap.jwt.JwtTokenProvider;
import com.joayong.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // WebSocket 헤더에서 토큰 가져오기
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // "Bearer " 부분 제거

                if (jwtTokenProvider.validateToken(token)) {
                    // 토큰에서 사용자 이름 가져오기
                    String email= jwtTokenProvider.getCurrentLoginUsername(token);
                    String username = userRepository.findByEmail(email).get().getName();
                    // Spring Security의 Authentication 객체 생성
                    UserDetails userDetails = new User(username, "", Collections.emptyList());
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

                    // WebSocket 세션에 사용자 인증 정보 저장
                    accessor.setUser(authentication);
                }
            }
        }
        return message;
    }
}