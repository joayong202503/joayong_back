package com.joayong.skillswap.domain.post.entity;

import com.joayong.skillswap.domain.category.entity.CategoryRegion;
import com.joayong.skillswap.domain.category.entity.CategoryTalent;
import com.joayong.skillswap.domain.image.entity.PostImageUrl;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString(exclude = "post")
@Builder
@Entity
@Table(name = "post_item_tb")
public class PostItem {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private final String id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @OneToOne
    @JoinColumn(name = "talent_g_id", referencedColumnName = "id", nullable = false)
    private CategoryTalent talentGId;

    @OneToOne
    @JoinColumn(name = "talent_t_id", referencedColumnName = "id", nullable = false)
    private CategoryTalent talentTId;

    @OneToOne
    @JoinColumn(name = "region_id", referencedColumnName = "id", nullable = false)
    private CategoryRegion regionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Builder.Default
    @OneToMany(mappedBy = "postItem",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<PostImageUrl> postImages = new ArrayList<>();

    public PostItem() {
        this.id = UUID.randomUUID().toString();
    }
}