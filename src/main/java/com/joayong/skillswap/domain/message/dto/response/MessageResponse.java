package com.joayong.skillswap.domain.message.dto.response;

import com.joayong.skillswap.domain.image.entity.MessageImageUrl;
import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.post.entity.PostItem;
import com.querydsl.core.Tuple;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    private String senderName;
    private String receiverName;
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
                .senderName(message.getSender().getName())
                .receiverName(message.getPost().getWriter().getName())
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

    public static MessageResponse toDtoPage(Tuple tuple) {
            return MessageResponse.builder()
                    .senderName(tuple.get(0,String.class))
                    .receiverName(tuple.get(1,String.class))
                    .messageId(tuple.get(2,String.class))
                    .postId(tuple.get(3,String.class))
                    .talentGive(tuple.get(4,String.class))
                    .talentTake(tuple.get(5,String.class))
                    .content(tuple.get(6,String.class))
                    .status(tuple.get(7,String.class))
                    .sentAt(tuple.get(8,LocalDateTime.class))
                    .isSend(tuple.get(9,Boolean.class))
                    .build();
    }
}
