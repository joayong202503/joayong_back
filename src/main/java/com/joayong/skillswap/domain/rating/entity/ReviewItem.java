package com.joayong.skillswap.domain.rating.entity;


import com.joayong.skillswap.domain.post.entity.PostItem;
import jakarta.persistence.*;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString(exclude = "ratingDetailList")
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

    @Builder.Default
    @OneToMany(mappedBy = "reviewItem", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RatingDetail> ratingDetailList = new ArrayList<>();

}
