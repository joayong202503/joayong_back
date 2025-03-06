package com.joayong.skillswap.domain.category.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "categoryTl")
@Builder
@Entity
@Table(name = "category_ts_tb")
public class CategoryTs {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tr_id", nullable = false)
    private CategoryTl categoryTl;

    @Column(nullable = false, length = 255)
    private String content;
}