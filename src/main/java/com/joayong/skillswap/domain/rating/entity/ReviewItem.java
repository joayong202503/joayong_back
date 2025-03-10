package com.joayong.skillswap.domain.rating.entity;


import jakarta.persistence.*;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Entity
@Table(name = "review_item_tb")
public class ReviewItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",updatable = false, nullable = false)
    private Long id;

    @Column(name = "question", nullable = false, length = 255)
    private String question;

    @ManyToOne
    @JoinColumn(name = "rating_detail_id", nullable = false)
    private RatingDetail ratingDetail;
}
