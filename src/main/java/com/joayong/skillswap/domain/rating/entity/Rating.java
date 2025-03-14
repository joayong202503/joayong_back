package com.joayong.skillswap.domain.rating.entity;

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
@NoArgsConstructor
@ToString(exclude = {"user", "ratingDetails"})
@Builder
@Entity
@Table(name = "rating_tb")
public class Rating {
    @Builder.Default
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id= UUID.randomUUID().toString();;

    @Builder.Default
    @Column(name = "total_rating", columnDefinition = "DOUBLE")
    private double totalRating = 0.0;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "rating", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RatingDetail> ratingDetails = new ArrayList<>();

    @OneToOne(mappedBy = "rating", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;

    public void setRatingDetails(List<RatingDetail> ratingDetails) {
        this.ratingDetails = ratingDetails;
    }

    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }
}