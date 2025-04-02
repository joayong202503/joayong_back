package com.joayong.skillswap.domain.category.entity;

import com.joayong.skillswap.domain.post.entity.PostItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"parent","children","postItem"})
@Builder
@Entity
@Table(name = "category_region_tb")
public class CategoryRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",updatable = false, nullable = false)
    private Long id;

    // 부모 카테고리 (Self-Join)
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CategoryRegion parent;

    // 자식 카테고리 리스트 (양방향 매핑)
    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryRegion> children = new ArrayList<>();

    @Column(nullable = false, length = 255)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "regionId")
    private List<PostItem> postItem= new ArrayList<>();

    // 자식 카테고리 추가 메서드
    public void addChild(CategoryRegion child) {
        children.add(child);
        child.setParent(this);
    }

    // 자식 카테고리 삭제 메서드
    public void removeChild(CategoryRegion child) {
        children.remove(child);
        child.setParent(null);
    }

    private void setParent(CategoryRegion parent) {
        this.parent = parent;
    }
}