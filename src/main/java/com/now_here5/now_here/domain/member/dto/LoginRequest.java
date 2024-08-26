package com.now_here5.now_here.domain.member.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Schema(description = "로그인 요청 DTO")
@Getter
@Builder
@RequiredArgsConstructor
public class LoginRequest {

    @Schema(description = "핸드폰 번호", example = "01012345678", required = true)
//    @NotNull(message = "휴대폰 번호를 입력해주세요")
//    @Size(min = 3, max = 50, message = "이메일은 3자 이상 50자 이하여야 합니다")
    private final String phone;
    
    @Schema(description = "비밀번호", example = "1234", required = true)
    private final String password;

}
