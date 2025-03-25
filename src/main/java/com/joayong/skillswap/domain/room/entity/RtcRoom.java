package com.joayong.skillswap.domain.room.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Table(name = "rtc_room_tb")
@Entity
public class RtcRoom {
    @Id
    private Integer roomId; // 방 번호

    @Column(name = "isAvailable")
    @Builder.Default
    private String isAvailable = null;
}
