package com.joayong.skillswap.domain.category.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.joayong.skillswap.domain.post.entity.PostItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"parent", "children","postItemGId","postItemTId"})
@Builder
@Entity
@Table(name = "category_talent_tb")
public class CategoryTalent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",updatable = false, nullable = false)
    private Long id;

    // 부모 카테고리 (Self-Join)
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CategoryTalent parent;

    // 자식 카테고리 리스트 (양방향 매핑)
    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryTalent> children = new ArrayList<>();

    @Column(nullable = false, length = 255)
    private String name;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "talentGId")
    private List<PostItem> postItemGId = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "talentTId")
    private List<PostItem> postItemTId= new ArrayList<>();

    // 자식 카테고리 추가 메서드
    public void addChild(CategoryTalent child) {
        children.add(child);
        child.setParent(this);
    }

    // 자식 카테고리 삭제 메서드
    public void removeChild(CategoryTalent child) {
        children.remove(child);
        child.setParent(null);
    }

    private void setParent(CategoryTalent parent) {
        this.parent = parent;
    }
}