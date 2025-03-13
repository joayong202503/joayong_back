package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.domain.image.dto.response.PostImageUrlResponse;
import com.joayong.skillswap.domain.image.entity.PostImageUrl;
import com.joayong.skillswap.domain.image.entity.QPostImageUrl;
import com.joayong.skillswap.domain.post.dto.response.PostResponse;
import com.joayong.skillswap.domain.post.entity.QPost;
import com.joayong.skillswap.domain.post.entity.QPostItem;
import com.joayong.skillswap.domain.user.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<PostResponse> findPosts(Pageable pageable) {
        QPost post = QPost.post;
        QPostItem postItem = QPostItem.postItem;
        QUser user = QUser.user;
        QPostImageUrl postImageUrl = QPostImageUrl.postImageUrl;

        // 메인 쿼리 (Post와 PostItem, User만 조인)
        List<PostResponse> posts = queryFactory
                .select(Projections.fields(PostResponse.class,
                        post.id,
                        postItem.title,
                        postItem.content,
                        postItem.id.as("postItemId"),
                        user.name,
                        user.email,
                        post.createdAt,
                        post.updatedAt,
                        post.viewCount
                ))
                .from(post)
                .where(post.deletedAt.isNull())
                .join(postItem).on(postItem.post.eq(post))
                .join(user).on(post.writer.eq(user))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // **더보기 방식 지원을 위해 +1 조회**
                .fetch();

        // PostItem과 PostImageUrl을 매핑하여 이미지 리스트 설정
        posts.forEach(postResponse -> {
            List<PostImageUrlResponse> images = queryFactory
                    .select(Projections.constructor(PostImageUrlResponse.class,
                            postImageUrl.imageUrl,
                            postImageUrl.id,
                            postImageUrl.sequence
                    ))
                    .from(postImageUrl)
                    .where(postImageUrl.postItem.id.eq(postResponse.getPostItemId()))
                    .orderBy(postImageUrl.sequence.asc())
                    .fetch();
            postResponse.setImages(images);
        });

        // **Slice 처리를 위해 마지막 데이터가 있는지 확인**
        boolean hasNext = false;
        if (posts.size() > pageable.getPageSize()) {
            posts.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }

    @Override
    public PostResponse findPostById(String id){
        QPost post = QPost.post;
        QPostItem postItem = QPostItem.postItem;
        QUser user = QUser.user;
        QPostImageUrl postImageUrl = QPostImageUrl.postImageUrl;

        PostResponse foundPost = queryFactory
                .select(Projections.fields(PostResponse.class,
                        post.id,
                        postItem.title,
                        postItem.content,
                        postItem.id.as("postItemId"),
                        user.name,
                        user.email,
                        post.createdAt,
                        post.updatedAt,
                        post.viewCount
                ))
                .from(post)
                .where(post.deletedAt.isNull())
                .where(post.id.eq(id))
                .join(postItem).on(postItem.post.eq(post))
                .join(user).on(post.writer.eq(user))
                .fetchOne();

        List<PostImageUrlResponse> images = queryFactory
                .select(Projections.constructor(PostImageUrlResponse.class,
                        postImageUrl.imageUrl,
                        postImageUrl.id,
                        postImageUrl.sequence
                ))
                .from(postImageUrl)
                .where(postImageUrl.postItem.id.eq(foundPost.getPostItemId()))
                .orderBy(postImageUrl.sequence.asc())
                .fetch();
        foundPost.setImages(images);

        return foundPost;
    }
}
