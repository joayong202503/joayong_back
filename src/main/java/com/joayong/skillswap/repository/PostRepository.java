package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,String> {
}
