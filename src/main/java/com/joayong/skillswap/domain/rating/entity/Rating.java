package com.joayong.skillswap.domain.rating.entity;

import com.joayong.skillswap.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
@Builder
@Entity
@Table(name = "rating_tb")
public class Rating {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36) DEFAULT UUID()")
    private final String id = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_rating")
    private double totalRating = 0.0;
}