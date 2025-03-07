package com.joayong.skillswap.domain.category.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"parent","children"})
@Builder
@Entity
@Table(name = "category_talent_tb")
public class CategoryTalent {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id = UUID.randomUUID().toString();

    // 부모 카테고리 (Self-Join)
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CategoryTalent parent;

    // 자식 카테고리 리스트 (양방향 매핑)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryTalent> children = new ArrayList<>();

    @Column(nullable = false, length = 255)
    private String name;
}