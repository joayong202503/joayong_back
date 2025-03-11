package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.domain.category.entity.CategoryTalent;
import com.querydsl.core.Tuple;

import java.util.List;

public interface CategoryTalentRepositoryCustom {

    public List<Tuple> getTalentCategory() ;
}
