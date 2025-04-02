package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.post.entity.PostItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostItemRepository extends JpaRepository<PostItem,String> {
}
