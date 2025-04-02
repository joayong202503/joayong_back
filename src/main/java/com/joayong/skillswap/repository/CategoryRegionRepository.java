package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.category.entity.CategoryRegion;
import com.joayong.skillswap.repository.custom.CategoryRegionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRegionRepository extends JpaRepository<CategoryRegion,Long>, CategoryRegionRepositoryCustom {
}
