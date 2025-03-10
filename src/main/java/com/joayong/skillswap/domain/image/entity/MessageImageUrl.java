package com.joayong.skillswap.domain.image.entity;

import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.PostItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Entity
@Table(name = "message_image_url_tb")
public class MessageImageUrl {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "sequence")
    private int sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_item_id")
    private Message message;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    public MessageImageUrl(){
        this.id  = UUID.randomUUID().toString();
    }
}
