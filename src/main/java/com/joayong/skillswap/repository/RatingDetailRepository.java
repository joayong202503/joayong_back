package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.rating.entity.Rating;
import com.joayong.skillswap.domain.rating.entity.RatingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingDetailRepository extends JpaRepository<RatingDetail,String> {
    List<RatingDetail> findByRating(Rating rating);

    @Query("SELECT DISTINCT r.post.id FROM RatingDetail r")
    List<String> findDistinctPostIds();

    @Query("SELECT DISTINCT r.message.id FROM RatingDetail r")
    List<String> findDistinctMessageIds();

    List<RatingDetail> findByPostId(String postId);
}
