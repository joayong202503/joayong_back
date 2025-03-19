package com.joayong.skillswap.domain.message.dto.response;

import com.joayong.skillswap.domain.image.entity.MessageImageUrl;
import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.post.entity.PostItem;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
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
    private boolean isSend;
    private LocalDateTime sentAt;


    public static MessageResponse toDto(Message message, boolean isSend) {


        MessageResponse build = MessageResponse.builder()
                .messageId(message.getId())
                .postId(message.getPost().getId())
                .talentGive(message.getPost().getPostItem().getTalentTId().getName())
                .talentTake(message.getPost().getPostItem().getTalentGId().getName())
                .content(message.getContent())
                .status(message.getMsgStatus().name())
                .isSend(isSend)
                .sentAt(message.getSentAt())
                .build();
        log.info("build : {}", build);
        return build;
    }
}
