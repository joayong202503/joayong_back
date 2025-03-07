package com.joayong.skillswap.domain.rating.entity;

import com.joayong.skillswap.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Getter
@AllArgsConstructor
@ToString(exclude = "user")
@Builder
@Entity
@Table(name = "rating_tb")
public class Rating {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private final String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_rating", columnDefinition = "DOUBLE")
    private double totalRating;

    public Rating(){
        this.id  = UUID.randomUUID().toString();
        this.totalRating = 0.0;
    }
}