package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.domain.category.entity.CategoryTalent;
import com.joayong.skillswap.domain.category.entity.QCategoryTalent;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CategoryTalentRepositoryImpl implements CategoryTalentRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public List<Tuple> getTalentCategory() {

        QCategoryTalent mainTalent = QCategoryTalent.categoryTalent;;
        QCategoryTalent subTalent = new QCategoryTalent("subTalent");;

        List<Tuple> talentCategoryTuple = factory.select(
                        mainTalent.id,
                        mainTalent.name,
                        subTalent.id,
                        subTalent.name
                )
                .from(mainTalent)
                .join(subTalent).on(mainTalent.id.eq(subTalent.parent.id))
                .fetch();

        return talentCategoryTuple;
    }
}
