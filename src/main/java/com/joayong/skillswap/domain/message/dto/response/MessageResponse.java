package com.joayong.skillswap.domain.message.dto.response;

import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.post.entity.PostItem;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class MessageResponse {
    private String messageId;
    private String postId;
    private String talentGive;
    private String talentTake;
    private String content;
    private String status;

    private List<String> imageUrlList;

    public static MessageResponse toDto(Message message) {


        MessageResponse build = MessageResponse.builder()
                .messageId(message.getId())
                .postId(message.getPost().getId())
                .talentGive(message.getPost().getPostItem().getTalentTId().getName())
                .talentTake(message.getPost().getPostItem().getTalentGId().getName())
                .content(message.getContent())
                .status(message.getMsgStatus().name())
                .build();
        log.info("build : {}", build);
        return build;
    }
}
