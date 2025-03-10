package com.joayong.skillswap.domain.image.entity;

import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.PostItem;
import jakarta.persistence.*;

import lombok.*;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Entity
@Table(name = "post_image_url_tb")
public class PostImageUrl {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private final String id;

    @Column(name = "sequence")
    private int sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_item_id")
    private PostItem postItem;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    public PostImageUrl(){
        this.id  = UUID.randomUUID().toString();
    }
}
