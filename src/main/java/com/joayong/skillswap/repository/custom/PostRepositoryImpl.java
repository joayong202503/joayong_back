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

    @Override
    public Slice<PostResponse> findPosts(Pageable pageable) {
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
    public List<PostResponse> findUserPosts(String id) {
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
        //업데이트전 내용
        Post original = queryFactory
                .selectFrom(post)
                .where(post.writer.id.eq(id))
                .where(post.id.eq(request.getPostId()))
                .fetchOne();
        //post 수정
        queryFactory
                .update(post)
                .set(post.updatedAt,LocalDateTime.now())
                .where(post.writer.id.eq(id))
                .where(post.id.eq(request.getPostId()))
                .execute();
        queryFactory
                .update(postItem)
                .set(postItem.title,request.getTitle())
                .set(postItem.content,request.getContent())
                .set(postItem.regionId.id,request.getRegionId())
                .set(postItem.talentGId.id,request.getTalentGId())
                .set(postItem.talentTId.id,request.getTalentTId())
                .where(postItem.post.id.eq(original.getId()))
                .execute();
        queryFactory
                .delete(postImageUrl)
                .where(postImageUrl.postItem.id.eq(original.getPostItem().getId()))
                .execute();

    }

    @Override
    public Slice<PostResponse> searchPosts(String keyword, Pageable pageable) {
        QPost post = QPost.post;
        QPostItem postItem = QPostItem.postItem;
        QUser user = QUser.user;
        QCategoryRegion region = QCategoryRegion.categoryRegion;
        QCategoryTalent talentT = new QCategoryTalent("talentT"); // 줄 재능 카테고리
        QCategoryTalent talentG = new QCategoryTalent("talentG"); // 받을 재능 카테고리
        QPostImageUrl postImageUrl = QPostImageUrl.postImageUrl;

        // 키워드 검색 조건
        BooleanExpression keywordPredicate = (keyword == null || keyword.trim().isEmpty())
                ? null
                : postItem.title.containsIgnoreCase(keyword)
                .or(postItem.content.containsIgnoreCase(keyword))
                .or(user.name.containsIgnoreCase(keyword))
                .or(region.name.containsIgnoreCase(keyword))
                .or(talentT.name.containsIgnoreCase(keyword))
                .or(talentG.name.containsIgnoreCase(keyword));

        // 메인 쿼리
        List<PostResponse> posts = queryFactory
                .select(Projections.fields(PostResponse.class,
                        post.id.stringValue(),
                        postItem.title,
                        postItem.content,
                        postItem.id.stringValue().as("postItemId"),
                        user.name,
                        user.email,
                        user.profileUrl.as("profileUrl"),
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
                .join(postItem).on(postItem.post.eq(post))
                .join(user).on(post.writer.eq(user))
                .join(postItem.regionId, region)
                .join(postItem.talentTId, talentT)
                .join(postItem.talentGId, talentG)
                .where(post.deletedAt.isNull()
                        .and(keywordPredicate))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        // 이미지 추가
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
        boolean hasNext = false;
        if (posts.size() > pageable.getPageSize()) {
            posts.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }
}
