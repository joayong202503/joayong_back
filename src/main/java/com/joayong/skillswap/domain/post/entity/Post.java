package com.joayong.skillswap.domain.post.entity;

import com.joayong.skillswap.domain.image.entity.PostImageUrl;
import com.joayong.skillswap.domain.match.entity.Match;
import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.enums.PostStatus;
import com.joayong.skillswap.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PostItem> postItemList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Message> messageList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Match> matchList = new ArrayList<>();

    public Post() {
        this.id = UUID.randomUUID().toString();
        this.status=PostStatus.N;
        this.viewCount = 0;
    }
}
