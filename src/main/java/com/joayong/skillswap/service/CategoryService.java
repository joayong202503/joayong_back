package com.joayong.skillswap.service;


import com.joayong.skillswap.domain.category.dto.response.*;
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
        List<MainRegionDto> regionCategoryList = getregionCategoryList();

        return CategoryResponse.builder()
                .mainTalentList(talentCategoryList)
                .mainRegionList(regionCategoryList)
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

    private List<MainRegionDto> getregionCategoryList() {
        List<Tuple> regionTupleList = regionRepository.getRegionCategory();

        log.info("regionTupleList : {} ", regionTupleList);

        Map<Long, MainRegionDto> mainRegionMap = new HashMap<>();
        Map<Long, SubRegionDto> subRegionMap = new HashMap<>();

        regionTupleList.forEach(tuple -> {
            Long mainRegionId = tuple.get(0, Long.class);
            String mainRegionName = tuple.get(1, String.class);
            Long subRegionId = tuple.get(2, Long.class);
            String subRegionName = tuple.get(3, String.class);
            Long detailRegionId = tuple.get(4, Long.class);
            String detailRegionName = tuple.get(5, String.class);

            // 1레벨
            MainRegionDto mainRegionDto = mainRegionMap.get(mainRegionId);

            if (mainRegionDto == null) {
                mainRegionDto = MainRegionDto.builder()
                        .id(mainRegionId)
                        .name(mainRegionName)
                        .build();
                mainRegionMap.put(mainRegionId, mainRegionDto);
            }

            // 2레벨
            SubRegionDto subRegionDto = subRegionMap.get(subRegionId);
            if(subRegionDto == null){
                subRegionDto = SubRegionDto.builder()
                        .id(subRegionId)
                        .name(subRegionName)
                        .build();
                subRegionMap.put(subRegionId,subRegionDto);
                mainRegionDto.getSubRegionList().add(subRegionDto);
            }

            // 3레벨
            DetailRegionDto detailRegionDto = DetailRegionDto.builder()
                    .id(detailRegionId)
                    .name(detailRegionName)
                    .build();
            subRegionDto.getDetailRegionList().add(detailRegionDto);
        });
        log.info("mainRegionMap : {} ", mainRegionMap);

        return mainRegionMap.values().stream().toList();
    }
}
