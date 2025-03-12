package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.rating.entity.ReviewItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewItemRepository extends JpaRepository<ReviewItem,String> {
}
