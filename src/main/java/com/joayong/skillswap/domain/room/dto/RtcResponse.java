package com.joayong.skillswap.domain.room.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class RtcResponse {
    private int roomId;
    private boolean isNew;
}
