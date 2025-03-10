package com.joayong.skillswap.domain.category.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CategoryResponse {

    @JsonProperty(namespace = "talent")
    private List<MainTalentDto> mainTalentList;

    @JsonProperty(namespace = "region")
    private  List<MainRegionDto> mainRegionList;
}
