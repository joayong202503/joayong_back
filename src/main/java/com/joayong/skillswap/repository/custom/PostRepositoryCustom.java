package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.domain.post.dto.response.PostResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface PostRepositoryCustom {
    Slice<PostResponse> findPosts(Pageable pageable);
}
