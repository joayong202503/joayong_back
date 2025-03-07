package com.joayong.skillswap.domain.match.entity;

import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString(exclude = {"post", "client"})
@Builder
@Entity
@Table(name = "match_tb")
public class Match {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private final String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @Column(name = "matched_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime matchedAt;

    public Match() {
        this.id = UUID.randomUUID().toString();
    }
}