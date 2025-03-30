package com.joayong.skillswap.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.joayong.skillswap.domain.image.dto.response.PostImageUrlResponse;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class PostSampleResponse {

    private String postId;
    private String writer;
    private String sender;
    private String talentTName;
    private String talentGName;

}
