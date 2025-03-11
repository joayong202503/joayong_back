package com.joayong.skillswap.domain.message.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class MessageRequest {

    private String postId;

    private String content;

    private List<MultipartFile> images;

}
