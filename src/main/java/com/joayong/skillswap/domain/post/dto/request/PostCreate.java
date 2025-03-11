package com.joayong.skillswap.domain.post.dto.request;

import com.joayong.skillswap.domain.post.entity.Post;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PostCreate {
    @Size(max = 2200, message = "게시글 내용은 최대 2200자까지 입력 가능합니다.")
    private String content;

    // 이미지 목록
    private List<MultipartFile> images;

    public Post toEntity() {
        return Post.builder()
//                .content(this.content)
                .build();
    }
}
