package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.domain.post.dto.request.PostCreateRequest;
import com.joayong.skillswap.domain.post.dto.request.UpdatePostRequest;
import com.joayong.skillswap.domain.post.dto.response.PostResponse;
import com.joayong.skillswap.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;


public interface PostRepositoryCustom {
    Page<PostResponse> findPosts(Pageable pageable);

    PostResponse findPostById(String id);

    long deletePost(String PostId, String id);

    List<PostResponse> findMyPosts(String id);

    List<PostResponse> findUserPosts(String userId);

    long viewCount(String postId);

    long viewCountWithId(String postId, String id);

    void updatePost(String id, UpdatePostRequest request);

    Page<PostResponse> searchPosts(String keyword,Pageable pageable);
}
