package com.joayong.skillswap.domain.message.entity;

import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.enums.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString(exclude = "post")
@Builder
@Entity
@Table(name = "message_tb")
public class Message {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private final String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "msg_status", nullable = false, columnDefinition = "VARCHAR(10)")
    private PostStatus msgStatus;

    @Column(name = "sent_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime sentAt;

    public Message(){
        this.id  = UUID.randomUUID().toString();
        this.msgStatus = PostStatus.N;
    }

}