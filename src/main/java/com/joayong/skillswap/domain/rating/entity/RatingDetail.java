package com.joayong.skillswap.domain.rating.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Table(name = "rating_detail_tb")
public class RatingDetail {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private final String id;

    @ManyToOne
    @JoinColumn(name = "rating_id", nullable = false)
    private Rating rating;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "value", nullable = false)
    private int ratingValue;

    public RatingDetail() {
        this.id = UUID.randomUUID().toString();
    }
}
