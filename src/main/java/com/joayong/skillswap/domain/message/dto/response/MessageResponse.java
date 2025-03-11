package com.joayong.skillswap.domain.message.dto.response;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class MessageResponse {
    private String messageId;
    private String postId;
    private String giveTalent;
    private String takeTalent;
    private String content;
    private String status;

    private List<String> imageUrlList;
}
