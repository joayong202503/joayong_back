package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.domain.category.entity.QCategoryRegion;
import com.joayong.skillswap.domain.category.entity.QCategoryTalent;
import com.joayong.skillswap.domain.image.dto.response.PostImageUrlResponse;
import com.joayong.skillswap.domain.image.entity.PostImageUrl;
import com.joayong.skillswap.domain.image.entity.QPostImageUrl;
import com.joayong.skillswap.domain.post.dto.request.PostCreateRequest;
import com.joayong.skillswap.domain.post.dto.request.UpdatePostRequest;
import com.joayong.skillswap.domain.post.dto.response.PostResponse;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.post.entity.QPost;
import com.joayong.skillswap.domain.post.entity.QPostItem;
import com.joayong.skillswap.domain.user.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    //전체조회로 페이지 반환
    @Override
    public Page<PostResponse> findPosts(Pageable pageable) {
        QPost post = QPost.post;
        QPostItem postItem = QPostItem.postItem;
        QUser user = QUser.user;
        QCategoryRegion region = QCategoryRegion.categoryRegion;
        QCategoryTalent talentT = new QCategoryTalent("talentT"); // 줄 재능 카테고리
        QCategoryTalent talentG = new QCategoryTalent("talentG"); // 받을 재능 카테고리
        QPostImageUrl postImageUrl = QPostImageUrl.postImageUrl;

        List<PostResponse> posts = queryFactory
                .select(Projections.fields(PostResponse.class,
                        post.id,
                        postItem.title,
                        postItem.content,
                        postItem.id.as("postItemId"),
                        postItem.talentTId.id.as("talentTId"),
                        talentT.name.as("talentTName"),
                        postItem.talentGId.id.as("talentGId"),
                        talentG.name.as("talentGName"),
                        postItem.regionId.id.as("regionId"),
                        region.name.as("regionName"),
                        user.profileUrl,
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
                .join(postItem.regionId, region)
                .join(postItem.talentTId, talentT)
                .join(postItem.talentGId, talentG)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(post.count())
                .from(post)
                .where(post.deletedAt.isNull())
                .fetchOne();

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

        return new PageImpl<>(posts, pageable, total);
    }

    //단일 게시물 id로 조회
    @Override
    public PostResponse findPostById(String id){
        QPost post = QPost.post;
        QPostItem postItem = QPostItem.postItem;
        QUser user = QUser.user;
        QCategoryRegion region = QCategoryRegion.categoryRegion;
        QCategoryTalent talentT = new QCategoryTalent("talentT"); // 줄 재능 카테고리
        QCategoryTalent talentG = new QCategoryTalent("talentG"); // 받을 재능 카테고리
        QPostImageUrl postImageUrl = QPostImageUrl.postImageUrl;

        PostResponse foundPost = queryFactory
                .select(Projections.fields(PostResponse.class,
                        post.id,
                        postItem.title,
                        postItem.content,
                        postItem.id.as("postItemId"),
                        user.name,
                        user.email,
                        user.profileUrl,
                        postItem.id.as("postItemId"),
                        postItem.talentTId.id.as("talentTId"),
                        talentT.name.as("talentTName"),
                        postItem.talentGId.id.as("talentGId"),
                        talentG.name.as("talentGName"),
                        postItem.regionId.id.as("regionId"),
                        region.name.as("regionName"),
                        post.createdAt,
                        post.updatedAt,
                        post.viewCount
                ))
                .from(post)
                .where(post.deletedAt.isNull())
                .where(post.id.eq(id))
                .join(postItem).on(postItem.post.eq(post))
                .join(user).on(post.writer.eq(user))
                .join(postItem.regionId, region)
                .join(postItem.talentTId, talentT)
                .join(postItem.talentGId, talentG)
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

    //논리적 삭제
    @Override
    public long deletePost(String postId,String id) {
        QPost post = QPost.post;
        return queryFactory
                .update(post)
                .set(post.deletedAt, LocalDateTime.now())
                .where(post.id.eq(postId))
                .where(post.writer.id.eq(id))
                .execute();
    }

    //내포스트 찾기
    @Override
    public List<PostResponse> findMyPosts(String id){
        QPost post = QPost.post;
        QPostItem postItem = QPostItem.postItem;
        QUser user = QUser.user;
        QCategoryRegion region = QCategoryRegion.categoryRegion;
        QCategoryTalent talentT = new QCategoryTalent("talentT"); // 줄 재능 카테고리
        QCategoryTalent talentG = new QCategoryTalent("talentG"); // 받을 재능 카테고리
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
                        user.profileUrl,
                        postItem.id.as("postItemId"),
                        postItem.talentTId.id.as("talentTId"),
                        talentT.name.as("talentTName"),
                        postItem.talentGId.id.as("talentGId"),
                        talentG.name.as("talentGName"),
                        postItem.regionId.id.as("regionId"),
                        region.name.as("regionName"),
                        post.createdAt,
                        post.updatedAt,
                        post.viewCount
                ))
                .from(post)
                .where(post.deletedAt.isNull())
                .where(post.writer.id.eq(id))
                .join(postItem).on(postItem.post.eq(post))
                .join(user).on(post.writer.eq(user))
                .join(postItem.regionId, region)
                .join(postItem.talentTId, talentT)
                .join(postItem.talentGId, talentG)
                .orderBy(post.createdAt.desc())
                .fetch();

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
        return posts;
    }
    //해당 유저 포스트 전체 조회
    @Override
    public List<PostResponse> findUserPosts(String name) {
        QPost post = QPost.post;
        QPostItem postItem = QPostItem.postItem;
        QUser user = QUser.user;
        QCategoryRegion region = QCategoryRegion.categoryRegion;
        QCategoryTalent talentT = new QCategoryTalent("talentT"); // 줄 재능 카테고리
        QCategoryTalent talentG = new QCategoryTalent("talentG"); // 받을 재능 카테고리
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
                        user.profileUrl,
                        postItem.id.as("postItemId"),
                        postItem.talentTId.id.as("talentTId"),
                        talentT.name.as("talentTName"),
                        postItem.talentGId.id.as("talentGId"),
                        talentG.name.as("talentGName"),
                        postItem.regionId.id.as("regionId"),
                        region.name.as("regionName"),
                        post.createdAt,
                        post.updatedAt,
                        post.viewCount
                ))
                .from(post)
                .where(post.deletedAt.isNull())
                .where(post.writer.name.eq(name))
                .join(postItem).on(postItem.post.eq(post))
                .join(user).on(post.writer.eq(user))
                .join(postItem.regionId, region)
                .join(postItem.talentTId, talentT)
                .join(postItem.talentGId, talentG)
                .orderBy(post.createdAt.desc())
                .fetch();

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
        return posts;
    }

    @Override
    public long viewCount(String postId){
        QPost post = QPost.post;
        return queryFactory
                .update(post)
                .set(post.viewCount,post.viewCount.add(1))
                .where(post.id.eq(postId))
                .execute();
    }

    @Override
    public long viewCountWithId(String postId, String id) {
        QPost post = QPost.post;
        return queryFactory
                .update(post)
                .set(post.viewCount,post.viewCount.add(1))
                .where(post.id.eq(postId)
                .and(post.writer.id.ne(id)))
                .execute();
    }

    @Override
    public void updatePost(String id, UpdatePostRequest request){
        QPost post = QPost.post;
        QPostItem postItem = QPostItem.postItem;
        QUser user = QUser.user;
        QPostImageUrl postImageUrl = QPostImageUrl.postImageUrl;

        // 업데이트 전 내용 조회
        Post original = queryFactory
                .selectFrom(post)
                .where(post.writer.id.eq(id))
                .where(post.id.eq(request.getPostId()))
                .fetchOne();

        // post 수정
        queryFactory
                .update(post)
                .set(post.updatedAt, LocalDateTime.now())
                .where(post.writer.id.eq(id))
                .where(post.id.eq(request.getPostId()))
                .execute();

        // postItem 수정
        queryFactory
                .update(postItem)
                .set(postItem.title, request.getTitle())
                .set(postItem.content, request.getContent())
                .set(postItem.regionId.id, request.getRegionId())
                .set(postItem.talentGId.id, request.getTalentGId())
                .set(postItem.talentTId.id, request.getTalentTId())
                .where(postItem.post.id.eq(original.getId()))
                .execute();

        // 이미지 삭제 처리
        if (request.getUpdateImage()) {
            if (request.getDeleteImageIds() != null && !request.getDeleteImageIds().isEmpty()) {
                // 선택된 이미지만 삭제
                queryFactory
                        .delete(postImageUrl)
                        .where(postImageUrl.postItem.id.eq(original.getPostItem().getId())
                                .and(postImageUrl.id.in(request.getDeleteImageIds())))
                        .execute();
            }
        }
    }

    @Override
    public Page<PostResponse> searchPosts(String keyword, Pageable pageable) {
        QPost post = QPost.post;
        QPostItem postItem = QPostItem.postItem;
        QUser user = QUser.user;
        QCategoryRegion region = QCategoryRegion.categoryRegion;
        QCategoryTalent talentT = new QCategoryTalent("talentT");
        QCategoryTalent talentG = new QCategoryTalent("talentG");
        QPostImageUrl postImageUrl = QPostImageUrl.postImageUrl;

        // ✅ 키워드 검색 조건
        BooleanExpression keywordPredicate = (keyword == null || keyword.trim().isEmpty())
                ? null
                : postItem.title.containsIgnoreCase(keyword)
                .or(postItem.content.containsIgnoreCase(keyword))
                .or(post.writer.name.containsIgnoreCase(keyword))
                .or(region.name.containsIgnoreCase(keyword))
                .or(talentT.name.containsIgnoreCase(keyword))
                .or(talentG.name.containsIgnoreCase(keyword));

        // ✅ 데이터 조회
        List<PostResponse> posts = queryFactory
                .select(Projections.fields(PostResponse.class,
                        post.id.as("id"),
                        postItem.title,
                        postItem.content,
                        postItem.id.as("postItemId"),
                        post.writer.name.as("name"),
                        post.writer.email.as("email"),
                        post.writer.profileUrl.as("profileUrl"),
                        postItem.talentTId.id.as("talentTId"),
                        talentT.name.as("talentTName"),
                        postItem.talentGId.id.as("talentGId"),
                        talentG.name.as("talentGName"),
                        postItem.regionId.id.as("regionId"),
                        region.name.as("regionName"),
                        post.createdAt,
                        post.updatedAt,
                        post.viewCount
                ))
                .from(post)
                .join(post.postItem, postItem)
                .join(post.writer, user)
                .join(postItem.regionId, region)
                .join(postItem.talentTId, talentT)
                .join(postItem.talentGId, talentG)
                .where(post.deletedAt.isNull()
                        .and(keywordPredicate))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // ✅ 전체 개수 조회 (totalCount)
        Long total = queryFactory
                .select(post.count())
                .from(post)
                .join(post.postItem, postItem)
                .join(post.writer, user)
                .join(postItem.regionId, region)
                .join(postItem.talentTId, talentT)
                .join(postItem.talentGId, talentG)
                .where(post.deletedAt.isNull()
                        .and(keywordPredicate))
                .fetchOne();

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

        return new PageImpl<>(posts, pageable, total != null ? total : 0L);
    }
}
