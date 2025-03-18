package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.domain.post.dto.response.PostResponse;
import com.joayong.skillswap.domain.rating.entity.QRating;
import com.joayong.skillswap.domain.user.dto.response.UserProfileResponse;
import com.joayong.skillswap.domain.user.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Coalesce;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public void updateProfileImage(String imageUrl, String email) {
        QUser user = QUser.user;

        queryFactory
                .update(user)
//                .set(member.profileImageUrl, imageUrl)
                .set(user.updatedAt, LocalDateTime.now())
                .where(user.email.eq(email))
                .execute();
    }

    @Override
    public UserProfileResponse getUserProfile(String name){
        QUser user = QUser.user;
        QRating rating = QRating.rating;

        return queryFactory
                .select(Projections.constructor(UserProfileResponse.class,
                        user.email,
                        user.name,
                        user.profileUrl,
                        rating.totalRating.coalesce(0.0)
                        ))
                .from(user)
                .leftJoin(user.rating,rating)
                .where(user.name.eq(name))
                .fetchOne();
    }

    @Override
    public long updateName(String email, String newName){
        QUser user = QUser.user;
        return queryFactory
                .update(user)
                .set(user.name,newName)
                .where(user.email.eq(email))
                .execute();
    }


}
