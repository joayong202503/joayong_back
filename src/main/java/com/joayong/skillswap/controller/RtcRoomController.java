package com.joayong.skillswap.controller;


import com.joayong.skillswap.domain.room.dto.RtcResponse;
import com.joayong.skillswap.service.RtcRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/joayong/room")
public class RtcRoomController {

    private final RtcRoomService rtcRoomService;

    @GetMapping("/{messageId}")
    public ResponseEntity<?> getRtcRoomCode(
            @PathVariable String messageId,
            @AuthenticationPrincipal String email
    ) {
        RtcResponse rtcRoomCode = rtcRoomService.getRtcRoomCode(messageId, email);

        return ResponseEntity.ok().body(rtcRoomCode);
    }


    @PostMapping("/{roomId}")
    public ResponseEntity<?> exitRtcRoomCode(
            @PathVariable int roomId,
            @AuthenticationPrincipal String email
    ) {
        int count = rtcRoomService.exitRtcRoomCode(roomId, email);

        return ResponseEntity.ok().body(Map.of("message", count));
    }
}
