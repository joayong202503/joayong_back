package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.domain.category.entity.QCategoryRegion;
import com.joayong.skillswap.domain.category.entity.QCategoryTalent;
import com.joayong.skillswap.domain.image.entity.QMessageImageUrl;
import com.joayong.skillswap.domain.message.entity.QMessage;
import com.joayong.skillswap.domain.post.entity.QPost;
import com.joayong.skillswap.domain.post.entity.QPostItem;
import com.joayong.skillswap.domain.user.entity.QUser;
import com.joayong.skillswap.enums.MessageStatus;
import com.joayong.skillswap.enums.MessageType;
import com.joayong.skillswap.enums.PostStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepositoryCustom {

    private final JPAQueryFactory factory;


    @Override
    public long countMessages(MessageType messageType, MessageStatus messageStatus, String email, Pageable pageable) {
        QMessage message = QMessage.message;
        QPost post = QPost.post;
        QPostItem postItem = QPostItem.postItem;
        QCategoryTalent talentGive = QCategoryTalent.categoryTalent;
        QCategoryTalent talentTake = QCategoryTalent.categoryTalent;
        QUser sender = QUser.user;
        QUser writer = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        // 보낸 메시지 필터링
        if (messageType == MessageType.SEND || messageType == MessageType.ALL) {
            builder.or(message.sender.eq(sender));
        }

        // 받은 메시지 필터링
        if (messageType == MessageType.RECEIVE || messageType == MessageType.ALL) {
            builder.or(message.post.writer.eq(writer));
        }

        // 상태 필터링 (N,M,D,C)
        if (messageStatus != null) {
            builder.and(message.msgStatus.eq(messageStatus));
        }

        // Count 쿼리 작성
        long count = Optional.ofNullable(factory
                        .select(message.count())
                        .from(message)
                        .join(post).on(post.id.eq(message.post.id))
                        .join(postItem).on(postItem.post.id.eq(post.id))
                        .join(talentGive).on(talentGive.eq(postItem.talentGId))
                        .join(talentTake).on(talentTake.eq(postItem.talentTId))
                        .join(sender).on(sender.eq(message.sender))
                        .join(writer).on(writer.eq(post.writer))
                        .where(builder)
                        .fetchOne())
                .orElse(0L);  // null일 경우 0L로 처리

        return count;
    }

    @Transactional
    @Override
    public List<Tuple> getMessageList(String email, MessageType messageType, MessageStatus messageStatus, Pageable pageable) {

        QMessage message = QMessage.message;
        QPost post = QPost.post;
        QPostItem postItem = QPostItem.postItem;
        QCategoryRegion region = QCategoryRegion.categoryRegion;
        QCategoryTalent talentGive = QCategoryTalent.categoryTalent;
        QCategoryTalent talentTake = QCategoryTalent.categoryTalent;
        QMessageImageUrl messageImageUrl = QMessageImageUrl.messageImageUrl;
        QUser sender = QUser.user;
        QUser writer = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        // 보낸 메시지 필터링
        if (messageType == MessageType.SEND || messageType == MessageType.ALL) {
            builder.or(message.sender.eq(sender));
        }

        // 받은 메시지 필터링
        if (messageType == MessageType.RECEIVE || messageType == MessageType.ALL) {
            builder.or(message.post.writer.eq(writer));
        }

        // 상태 필터링 (N,M,D,C)
        if (messageStatus != null) {
            builder.and(message.msgStatus.eq(messageStatus));
        }

        List<Tuple> fetch = factory.select(
                        sender.name,
                        writer.name,
                        message.id,
                        post.id,
                        talentGive.name,
                        talentTake.name,
                        message.content,
                        message.msgStatus,
                        message.sentAt,
                        // 보낸사람이면 true 반환
                        new CaseBuilder()
                                .when(sender.email.eq(email))
                                .then(true)
                                .otherwise(false)

                )
                .from(message)
                .join(post).on(post.id.eq(message.post.id))
                .join(postItem).on(postItem.post.id.eq(post.id))
                .join(talentGive).on(talentGive.eq(postItem.talentGId))
                .join(talentTake).on(talentTake.eq(postItem.talentTId))
                .join(sender).on(sender.eq(message.sender))
                .join(writer).on(writer.eq(post.writer))
                .where(builder)
                .orderBy(message.sentAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return fetch;
    }
}
