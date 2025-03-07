package com.joayong.skillswap.domain.post.entity;

import com.joayong.skillswap.enums.PostStatus;
import com.joayong.skillswap.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString(exclude = "writer")
@Builder
@Entity
@Table(name = "post_tb")
public class Post {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private final String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer", nullable = false)
    private User writer;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "view_count")
    private Integer viewCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;

    public Post() {
        this.id = UUID.randomUUID().toString();
        this.status=PostStatus.N;
        this.viewCount = 0;
    }
}
