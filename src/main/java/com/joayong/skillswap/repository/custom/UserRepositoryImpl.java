package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.domain.rating.entity.QRating;
import com.joayong.skillswap.domain.rating.entity.QRatingDetail;
import com.joayong.skillswap.domain.user.dto.response.RatingDetailDto;
import com.joayong.skillswap.domain.user.dto.response.UserProfileResponse;
import com.joayong.skillswap.domain.user.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public UserProfileResponse getUserProfile(String id) {
        QUser user = QUser.user;
        QRating rating = QRating.rating;
        QRatingDetail ratingDetail = QRatingDetail.ratingDetail;

        return queryFactory
                .select(Projections.constructor(UserProfileResponse.class,
                        user.email,
                        user.name,
                        user.profileUrl,
                        rating.totalRating,
                        Projections.list(
                                Projections.constructor(RatingDetailDto.class,
                                        ratingDetail.id,
                                        ratingDetail.ratingValue,
                                        ratingDetail.createdAt,
                                        ratingDetail.updatedAt
                                )
                        )
                ))
                .from(user)
                .leftJoin(user.rating, rating)
                .leftJoin(rating.ratingDetails, ratingDetail)
                .where(user.id.eq(id))
                .fetchOne();
    }
}
