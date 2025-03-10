package com.joayong.skillswap.domain.talent.entity;

import com.joayong.skillswap.domain.category.entity.CategoryTalent;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.enums.TalentType;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user","category"})
@Builder
@Entity
@Table(name = "talent_tb")
public class Talent {
    @Builder.Default
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id= UUID.randomUUID().toString();;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private TalentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "value_id", nullable = false)
    private CategoryTalent category;
}
