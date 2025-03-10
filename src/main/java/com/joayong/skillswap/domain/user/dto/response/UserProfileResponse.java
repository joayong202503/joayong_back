package com.joayong.skillswap.domain.user.dto.response;

import com.joayong.skillswap.domain.rating.entity.Rating;
import com.joayong.skillswap.domain.rating.entity.RatingDetail;
import com.joayong.skillswap.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private String email;
    private String name;
    private String profileImageUrl;
    private double totalRating;
    private List<RatingDetailDto> ratingDetails;

    public static UserProfileResponse of(User user, Rating rating){
        return UserProfileResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .profileImageUrl(user.getProfileUrl())
                .totalRating(rating != null ? rating.getTotalRating() : 0.0)  // Null 체크 추가
                .ratingDetails(
                        rating != null && rating.getRatingDetails() != null
                                ? rating.getRatingDetails().stream().map(RatingDetailDto::of).toList()
                                : List.of()  // Null 체크 후 빈 리스트 반환
                )
                .build();
    }

}
