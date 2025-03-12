package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.rating.entity.RatingDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingDetailRepository extends JpaRepository<RatingDetail,String> {
}
