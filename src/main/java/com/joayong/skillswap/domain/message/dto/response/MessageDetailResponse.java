package com.joayong.skillswap.domain.message.dto.response;

import com.joayong.skillswap.domain.image.entity.MessageImageUrl;
import com.joayong.skillswap.domain.message.entity.Message;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class MessageDetailResponse {
    private String messageId;
    private List<MessageUrlResponse> messageUrlList;

    public static MessageDetailResponse toDto(Message message) {

        List<MessageUrlResponse> urlResponseList
                = message.getMessageImages().stream().map(MessageImageUrl::toDto).toList();

        return MessageDetailResponse.builder()
                .messageId(message.getId())
                .messageUrlList(urlResponseList)
                .build();
    }
}
