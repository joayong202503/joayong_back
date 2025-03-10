package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public void updateProfileImage(String imageUrl, String name) {
        QUser user = QUser.user;

        queryFactory
                .update(user)
//                .set(member.profileImageUrl, imageUrl)
                .set(user.updatedAt, LocalDateTime.now())
                .where(user.name.eq(name))
                .execute();
    }
}
