package com.joayong.skillswap.domain.category.dto.response;

import java.util.List;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class MainRegionDto {
    private Long id;

    private String name;

    private List<SubRegionDto> subRegionList;
}
