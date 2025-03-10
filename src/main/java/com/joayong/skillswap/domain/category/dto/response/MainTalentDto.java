package com.joayong.skillswap.domain.category.dto.response;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class MainTalentDto {
    private String name;

    private List<SubTalentDto> subTalentList;
}
