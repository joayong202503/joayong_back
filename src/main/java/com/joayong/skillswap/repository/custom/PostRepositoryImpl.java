package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.domain.image.entity.QPostImageUrl;
import com.joayong.skillswap.domain.post.dto.response.PostResponse;
import com.joayong.skillswap.domain.post.entity.Post;
import com.joayong.skillswap.domain.post.entity.PostItem;
import com.joayong.skillswap.domain.post.entity.QPost;
import com.joayong.skillswap.domain.post.entity.QPostItem;
import com.joayong.skillswap.domain.user.entity.QUser;
import com.joayong.skillswap.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

public class PostRepositoryImpl implements PostRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public void getAllPosts(){
        QPost post = QPost.post;
        QPostItem postItem = QPostItem.postItem;
        QUser user = QUser.user;
        QPostImageUrl postImageUrl = QPostImageUrl.postImageUrl;

        // Post + PostItem + User를 조인
        List<PostResponse> results = queryFactory
                .select(post, postItem, user)
                .from(post)
                .join(post.postItem, postItem)
                .join(post.writer, user)
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(tuple -> {
                    Post p = tuple.get(post);
                    PostItem pi = tuple.get(postItem);
                    User u = tuple.get(user);

                    // 해당 PostItem에 속한 이미지 가져오기
                    List<String> imageUrls = queryFactory
                            .select(postImageUrl.imageUrl)
                            .from(postImageUrl)
                            .where(postImageUrl.postItem.eq(pi))
                            .fetch();

                    return PostResponse.builder()
                            .id(p.getId())
                            .title(pi.getTitle())
                            .content(pi.getContent())
                            .name(u.getName())
                            .email(u.getEmail())
                            .images(imageUrls) // 이미지 리스트 추가
                            .createdAt(p.getCreatedAt())
                            .updatedAt(p.getUpdatedAt())
                            .viewCount(p.getViewCount())
                            .build();
                })
                .collect(Collectors.toList());

        long total = queryFactory
                .select(post.count())
                .from(post)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}
