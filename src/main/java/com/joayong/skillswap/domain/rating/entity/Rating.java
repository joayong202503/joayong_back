package com.joayong.skillswap.domain.rating.entity;

import com.joayong.skillswap.domain.talent.entity.Talent;
import com.joayong.skillswap.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@AllArgsConstructor
@ToString(exclude = {"user", "ratingDetails"})
@Builder
@Entity
@Table(name = "rating_tb")
public class Rating {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private final String id;

    @Column(name = "total_rating", columnDefinition = "DOUBLE")
    private double totalRating;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "rating", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RatingDetail> ratingDetails = new ArrayList<>();

    @OneToOne(mappedBy = "rating", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;

    public Rating() {
        this.id = UUID.randomUUID().toString();
        this.totalRating = 0.0;
    }
}