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
@ToString(exclude = {"user","categoryTs"})
@Builder
@Entity
@Table(name = "talent_tb")
public class Talent {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private TalentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "value_id", nullable = false)
    private CategoryTalent categoryTs;

    public Talent(){
        this.id  = UUID.randomUUID().toString();
    }
}
