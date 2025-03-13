package com.joayong.skillswap.domain.message.entity;

import com.joayong.skillswap.domain.image.entity.MessageImageUrl;
import com.joayong.skillswap.domain.image.entity.PostImageUrl;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.rating.entity.RatingDetail;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.enums.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"post", "messageImages","sender","ratingDetailList"})
@Builder
@Entity
@Table(name = "message_tb")
public class Message {
    @Builder.Default
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id= UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "content", nullable = false)
    private String content;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "msg_status", nullable = false, columnDefinition = "VARCHAR(10)")
    private PostStatus msgStatus = PostStatus.N;

    @Column(name = "sent_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime sentAt;

    @OneToMany(mappedBy = "message",cascade = CascadeType.REMOVE,orphanRemoval = true)
    @Builder.Default
    private List<MessageImageUrl> messageImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "message")
    private List<RatingDetail> ratingDetailList = new ArrayList<>();

}