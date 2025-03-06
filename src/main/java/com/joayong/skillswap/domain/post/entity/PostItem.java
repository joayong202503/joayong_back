package com.joayong.skillswap.domain.post.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString(exclude = "post")
@Builder
@Entity
@Table(name = "post_item_tb")
public class PostItem {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "talent_g", length = 255)
    private String talentG;

    @Column(name = "talent_t", length = 255)
    private String talentT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}