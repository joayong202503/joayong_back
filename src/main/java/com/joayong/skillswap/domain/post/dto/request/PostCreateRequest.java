package com.joayong.skillswap.domain.post.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.joayong.skillswap.domain.image.entity.PostImageUrl;
import com.joayong.skillswap.domain.post.entity.Post;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PostCreateRequest {

    private String title;
    @Size(max = 2200, message = "게시글 내용은 최대 2200자까지 입력 가능합니다.")
    private String content;
    @JsonProperty("talent-g-id")
    private Long talentGId;
    @JsonProperty("talent-t-id")
    private Long talentTId;
    @JsonProperty("region-id")
    private Long regionId;

}
