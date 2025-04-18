package com.joayong.skillswap.domain.user.entity;

import com.joayong.skillswap.domain.image.entity.PostImageUrl;
import com.joayong.skillswap.domain.match.entity.Match;
import com.joayong.skillswap.domain.message.entity.Message;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.rating.entity.Rating;
import com.joayong.skillswap.domain.rating.entity.RatingDetail;
import com.joayong.skillswap.domain.talent.entity.Talent;
import com.joayong.skillswap.enums.Role;
import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"talents","postList","rating","matchList","messageList","ratingDetailList"})
@Builder
@Entity
@Table(name = "user_tb")
public class User {

    @Builder.Default
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id= UUID.randomUUID().toString();;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role = Role.COMMON;

    @Column(name = "profile_url", length = 500)
    private String profileUrl;

    @Column(name = "talent-g-id")
    private Long talentGId;

    @Column(name = "talent-t-id")
    private Long talentTId;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Talent> talents = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "sender",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Message> messageList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "writer",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "rating_id")
    private Rating rating;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<RatingDetail> ratingDetailList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "client",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Match> matchList = new ArrayList<>();
}

