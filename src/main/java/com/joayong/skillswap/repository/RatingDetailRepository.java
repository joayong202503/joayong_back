package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.rating.entity.Rating;
import com.joayong.skillswap.domain.rating.entity.RatingDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingDetailRepository extends JpaRepository<RatingDetail,String> {
    List<RatingDetail> findByRating(Rating rating);
}
