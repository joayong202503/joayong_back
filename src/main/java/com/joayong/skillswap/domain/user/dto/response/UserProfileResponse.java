package com.joayong.skillswap.domain.user.dto.response;

import com.joayong.skillswap.domain.category.entity.CategoryTalent;
import com.joayong.skillswap.domain.rating.entity.Rating;
import com.joayong.skillswap.domain.rating.entity.RatingDetail;
import com.joayong.skillswap.domain.user.entity.User;
import lombok.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class UserProfileResponse {
    private String email;
    private String name;
    private String profileImageUrl;
    private double totalRating;
    private Long talentGId;
    private Long talentTId;

    public static UserProfileResponse of(User user, Rating rating){
        return UserProfileResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .profileImageUrl(user.getProfileUrl())
                .totalRating(rating != null ? rating.getTotalRating() : 0.0)  // Null 체크 추가
                .talentGId(user.getTalentGId())
                .talentTId(user.getTalentTId())
                .build();
    }

}
