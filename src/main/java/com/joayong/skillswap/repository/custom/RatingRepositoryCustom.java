package com.joayong.skillswap.repository.custom;

import com.querydsl.core.Tuple;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepositoryCustom {

    List<Tuple> getRatingList(String ratingId, Pageable pageable);


    long countDistinctMessageIdsByRatingId(String ratingId);
}
