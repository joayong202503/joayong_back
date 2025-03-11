package com.joayong.skillswap.repository;

import com.joayong.skillswap.domain.category.entity.CategoryTalent;
import com.joayong.skillswap.repository.custom.CategoryTalentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryTalentRepository extends JpaRepository<CategoryTalent, Long> , CategoryTalentRepositoryCustom {

}
