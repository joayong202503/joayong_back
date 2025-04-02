package com.joayong.skillswap.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTalentRequest {
    @JsonProperty("talent-g-id")
    private Long talentGId;
    @JsonProperty("talent-t-id")
    private Long talentTId;
}
