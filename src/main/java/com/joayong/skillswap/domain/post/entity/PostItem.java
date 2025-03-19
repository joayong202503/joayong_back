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
@NoArgsConstructor
@EqualsAndHashCode
@ToString(exclude = {"post", "talentGId", "talentTId", "regionId","postImages"})
@Builder
@Entity
@Table(name = "post_item_tb")
public class PostItem {
    @Builder.Default
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id= UUID.randomUUID().toString();;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, length=2200)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talent_g_id", referencedColumnName = "id", nullable = false)
    private CategoryTalent talentGId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talent_t_id", referencedColumnName = "id", nullable = false)
    private CategoryTalent talentTId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", referencedColumnName = "id", nullable = false)
    private CategoryRegion regionId;

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Builder.Default
    @OneToMany(mappedBy = "postItem",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<PostImageUrl> postImages = new ArrayList<>();
}