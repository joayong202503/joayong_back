package com.joayong.skillswap.domain.category.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class MainTalentDto {

    private Long id;

    private String name;

    @Builder.Default
    private List<SubTalentDto> subTalentList = new ArrayList<>();
}
