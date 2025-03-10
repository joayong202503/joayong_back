package com.joayong.skillswap.domain.user.dto.request;

import com.joayong.skillswap.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
//    @Pattern(regexp = "^[a-zA-Z0-9._]{4,20}$",
//            message = "사용자 이름은 4-20자의 영문, 숫자, 밑줄, 마침표만 사용 가능합니다")
    private String name;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 8자 이상, 영문과 숫자 조합이어야 합니다")
    private String password;

    // 클라이언트가 전송한 입력값들을 엔터티로 변환
    public User toEntity() {

        return User.builder()
                .email(email)
                .name(name)
                .password(password)
                .build();
    }
}
