package com.joayong.skillswap.controller;

import com.joayong.skillswap.domain.message.dto.response.MessageResponse;
import com.joayong.skillswap.domain.rating.dto.request.RatingRequest;
import com.joayong.skillswap.domain.rating.dto.response.RatingResponse;
import com.joayong.skillswap.dto.common.PageResponse;
import com.joayong.skillswap.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    ) {
        log.info("email:{}", email);
        log.info("dto : {}", dto);
        double v = ratingService.addRating(email, dto);
        return ResponseEntity.ok().body(v);
    }

    @GetMapping
    public ResponseEntity<?> getReview(
            @RequestParam String username
    ) {
        RatingResponse ratingList = ratingService.getRatingList(username);

        return ResponseEntity.ok().body(ratingList);
    }

    @GetMapping("/paging")
    public ResponseEntity<PageResponse<RatingResponse>> getReview(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {

        Pageable pageable = PageRequest.of(page,size);

        PageResponse<RatingResponse> pagingRatingList = ratingService.getPagingRatingList(username, pageable);

        return ResponseEntity.ok().body(pagingRatingList);
    }
}
