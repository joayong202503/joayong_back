package com.joayong.skillswap.controller;

import com.joayong.skillswap.domain.rating.dto.request.RatingRequest;
import com.joayong.skillswap.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/joayong/review")
@RestController
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<?> addReview(
            @RequestBody RatingRequest dto,
            @AuthenticationPrincipal String email
    ){
        log.info("email:{}",email);
        log.info("dto : {}",dto);
        double v = ratingService.addRating(email, dto);
        return ResponseEntity.ok().body(v);
    }
}
