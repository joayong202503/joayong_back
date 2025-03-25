package com.joayong.skillswap.controller;


import com.joayong.skillswap.service.RtcRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/joayong/room")
public class RtcRoomController {

    private final RtcRoomService rtcRoomService;

    @GetMapping("/{messageId}")
    public ResponseEntity<?> getRtcRoomCode(
            @PathVariable String messageId ,
            @AuthenticationPrincipal String email
    ){

        int rtcRoomCode = rtcRoomService.getRtcRoomCode(messageId, email);

        return ResponseEntity.ok().body(rtcRoomCode);
    }
}
