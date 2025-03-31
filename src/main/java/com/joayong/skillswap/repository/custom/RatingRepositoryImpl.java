package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.domain.message.entity.QMessage;
import com.joayong.skillswap.domain.rating.entity.QRating;
import com.joayong.skillswap.domain.rating.entity.QRatingDetail;
import com.joayong.skillswap.domain.rating.entity.QReviewItem;
import com.joayong.skillswap.domain.user.entity.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RatingRepositoryImpl implements RatingRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public List<Tuple> getRatingList(String ratingId, Pageable pageable) {

        QRating rating = QRating.rating;
        QRatingDetail ratingDetail = QRatingDetail.ratingDetail;
        QReviewItem reviewItem = QReviewItem.reviewItem;
        QUser user = QUser.user;
        QMessage message = QMessage.message;

        List<Tuple> fetch = factory.select(
                        rating.totalRating,         //0
                        ratingDetail.id,            //1
                        ratingDetail.reviewItem.id, //2
                        reviewItem.question,        //3
                        ratingDetail.ratingValue,   //4
                        ratingDetail.post.id,       //5
                        ratingDetail.message.id,    //6
                        user.name,                  //7
                        user.profileUrl,            //8
                        ratingDetail.createdAt      //9
                )
                .from(rating)
                .leftJoin(ratingDetail).on(rating.id.eq(ratingDetail.rating.id))
                .join(reviewItem).on(ratingDetail.reviewItem.id.eq(reviewItem.id))
                .join(user).on(ratingDetail.user.id.eq(user.id))
                .where(rating.id.eq(ratingId)
                        .and(rating.user.id.ne(ratingDetail.user.id))
                )
                .orderBy(
                        ratingDetail.createdAt.desc(),
                        ratingDetail.message.id.asc(),
                        ratingDetail.reviewItem.id.asc())
                .offset(pageable.getOffset()*5)
                .limit(pageable.getPageSize()*5)
                .fetch();

        log.info("fetch : {}",fetch);

        return fetch;

    }

    @Override
    public long countDistinctMessageIdsByRatingId(String ratingId) {
        QRating rating = QRating.rating;
        QRatingDetail ratingDetail = QRatingDetail.ratingDetail;
        QReviewItem reviewItem = QReviewItem.reviewItem;
        QUser user = QUser.user;

        log.info("ratingId dddd: {}",ratingId);
        long count = factory.select(ratingDetail.message.id.countDistinct()) // 메시지 ID의 중복 제거 후 개수 카운트
                .from(rating)
                .leftJoin(ratingDetail).on(rating.id.eq(ratingDetail.rating.id))
                .join(reviewItem).on(ratingDetail.reviewItem.id.eq(reviewItem.id))
                .join(user).on(ratingDetail.user.id.eq(user.id))
                .where(
                        rating.id.eq(ratingId)
                                .and(rating.user.id.ne(ratingDetail.user.id)) // 내 것 제외 조건
                )
                .fetchOne();

        log.info("count : {}",count);

        return count;
    }


}
