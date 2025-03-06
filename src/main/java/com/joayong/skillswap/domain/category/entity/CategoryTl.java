package com.joayong.skillswap.domain.category.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "categoryTsList")
@Builder
@Entity
@Table(name = "category_tl_tb")
public class CategoryTl {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, length = 255)
    private String content;

    @OneToMany(mappedBy = "categoryTl", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryTs> categoryTsList = new ArrayList<>();
}
