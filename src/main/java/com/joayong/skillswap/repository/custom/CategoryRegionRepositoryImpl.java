package com.joayong.skillswap.repository.custom;

import com.joayong.skillswap.domain.category.entity.QCategoryRegion;
import com.joayong.skillswap.domain.category.entity.QCategoryTalent;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class CategoryRegionRepositoryImpl implements CategoryRegionRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public List<Tuple> getRegionCategory() {
        QCategoryRegion mainRegion = QCategoryRegion.categoryRegion;
        ;
        QCategoryRegion subRegion = new QCategoryRegion("subRegion");
        ;
        QCategoryRegion detailRegion = new QCategoryRegion("detailRegion");
        ;

        List<Tuple> regionCategoryTuple = factory.select(
                        mainRegion.id,
                        mainRegion.name,
                        subRegion.id,
                        subRegion.name,
                        detailRegion.id,
                        detailRegion.name
                )
                .from(mainRegion)
                .join(subRegion).on(mainRegion.id.eq(subRegion.parent.id))
                .join(detailRegion).on(subRegion.id.eq(detailRegion.parent.id))
                .fetch();

        return regionCategoryTuple;
    }
}
