package com.joayong.skillswap.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.joayong.skillswap.domain.image.dto.response.PostImageUrlResponse;
import com.joayong.skillswap.domain.image.entity.PostImageUrl;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.post.entity.PostItem;
import com.joayong.skillswap.domain.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class PostResponse {
    @JsonProperty("post-id")
    private String id;
    private String title;
    private String content;
    private String name;
    private String email;
    @JsonProperty("talent-t-name")
    private String talentTName;
    @JsonProperty("talent-g-id")
    private Long talentGId;
    @JsonProperty("talent-t-id")
    private Long talentTId;
    @JsonProperty("talent-g-name")
    private String talentGName;
    @JsonProperty("region-id")
    private Long regionId;
    @JsonProperty("region-name")
    private String regionName;
    @JsonProperty("post-item-id")
    private String postItemId;
    @JsonProperty("profile-url")
    private String profileUrl;
    private List<PostImageUrlResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int viewCount;

//    public static PostResponse of(Post post, PostItem postItem, PostImageUrl postImageUrl, User user){
//        return PostResponse.builder()
//                .id(post.getId())
//                .title(postItem.getTitle())
//                .content(postItem.getContent())
//                .name(user.getName())
//                .email(user.getEmail())
//                .images(postItem.getPostImages())
//                .createdAt(post.getCreatedAt())
//                .updatedAt(post.getUpdatedAt())
//                .build();
//    }
}
