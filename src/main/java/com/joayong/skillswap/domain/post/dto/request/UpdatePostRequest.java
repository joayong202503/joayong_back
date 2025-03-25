package com.joayong.skillswap.domain.post.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UpdatePostRequest {

    @JsonProperty("post-id")
    private String postId;
    private String title;
    @Size(max = 2200, message = "게시글 내용은 최대 2200자까지 입력 가능합니다.")
    private String content;
    @JsonProperty("talent-g-id")
    private Long talentGId;
    @JsonProperty("talent-t-id")
    private Long talentTId;
    @JsonProperty("region-id")
    private Long regionId;
    @JsonProperty("update-image")
    private Boolean updateImage;
}
