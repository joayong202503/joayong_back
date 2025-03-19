package com.joayong.skillswap.repository.custom;

import com.querydsl.core.Tuple;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RatingRepositoryCustom {

    List<Tuple> getRatingList(String ratingId, Pageable pageable);
}
