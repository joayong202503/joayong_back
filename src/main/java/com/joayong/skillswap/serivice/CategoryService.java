package com.joayong.skillswap.serivice;


import com.joayong.skillswap.domain.category.dto.response.CategoryResponse;
import com.joayong.skillswap.domain.category.dto.response.MainRegionDto;
import com.joayong.skillswap.domain.category.dto.response.MainTalentDto;
import com.joayong.skillswap.domain.category.dto.response.SubTalentDto;
import com.joayong.skillswap.domain.category.entity.CategoryTalent;
import com.joayong.skillswap.domain.category.entity.QCategoryTalent;
import com.joayong.skillswap.repository.CategoryRegionRepository;
import com.joayong.skillswap.repository.CategoryTalentRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {

    private final CategoryRegionRepository regionRepository;
    private final CategoryTalentRepository talentRepository;

    public CategoryResponse getCategories() {

        List<MainTalentDto> talentCategoryList = getTalentCategoryList();

        return CategoryResponse.builder()
                .mainTalentList(talentCategoryList)
                .build();
    }

    private List<MainTalentDto> getTalentCategoryList() {
        List<Tuple> talentTupleList = talentRepository.getTalentCategory();

        log.info("talentTupleList : {} ", talentTupleList);

        Map<Long, MainTalentDto> mainTalentMap = new HashMap<>();

        talentTupleList.forEach(tuple -> {
            Long mainTalentId = tuple.get(0, Long.class);
            String mainTalentName = tuple.get(1, String.class);
            Long subTalentId = tuple.get(2, Long.class);
            String subTalentName = tuple.get(3, String.class);

            MainTalentDto mainTalentDto = mainTalentMap.get(mainTalentId);

            if (mainTalentDto == null) {
                mainTalentDto = MainTalentDto
                        .builder().id(mainTalentId)
                        .name(mainTalentName)
                        .build();
                mainTalentMap.put(mainTalentId, mainTalentDto);
            }
            SubTalentDto subTalentDto = SubTalentDto.builder()
                    .id(subTalentId)
                    .name(subTalentName)
                    .build();
            mainTalentDto.getSubTalentList().add(subTalentDto);
        });
        log.info("mainTalentMap : {} ", mainTalentMap);

        return mainTalentMap.values().stream().toList();
    }
}
