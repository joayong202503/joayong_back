package com.joayong.skillswap.domain.category.dto.response;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class SubRegionDto {

    private Long id;

    private String name;

    @Builder.Default
    private List<DetailRegionDto> detailRegionList = new ArrayList<>();
}
