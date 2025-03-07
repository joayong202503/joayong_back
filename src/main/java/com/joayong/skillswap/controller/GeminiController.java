package com.joayong.skillswap.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GeminiController {
    private String geminiService;

    @GetMapping("/api/gemini/dream-interpretation")
    public ResponseEntity<Map<String,Object>> getDreamInterpretation(
            @ModelAttribute String request,
            @AuthenticationPrincipal String id
    ) {

        return ResponseEntity
                .ok()
                .body(geminiService.geminiresponse(request));
    }
}
