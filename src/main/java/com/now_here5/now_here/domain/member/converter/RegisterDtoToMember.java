package com.now_here5.now_here.domain.member.converter;


import com.now_here5.now_here.domain.member.dto.RegisterMemberRequest;
import com.now_here5.now_here.domain.member.entity.Gender;
import com.now_here5.now_here.domain.member.entity.Mbti;
import com.now_here5.now_here.domain.member.entity.Member;
import com.now_here5.now_here.global.security.provider.TokenGenerator;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Builder
@Getter
@Configuration
@RequiredArgsConstructor
public class RegisterDtoToMember {
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    public Member converter(RegisterMemberRequest registerRequest) {
        String encryptedPassword = passwordEncoder.encode(registerRequest.getPassword());
        return Member.builder()
                .phoneNumber(registerRequest.getPhone())
                .password(encryptedPassword)
                .nickname(registerRequest.getNickname())
                .gender(Gender.valueOf(registerRequest.getGender().toUpperCase()))
                .notification(true)
                .active(true)
                .mbti(Mbti.valueOf(registerRequest.getMbti().toUpperCase()))
                .token(tokenGenerator.generateUniqueToken())
                .description(registerRequest.getDescription())
                .birthday(registerRequest.getBirth())
                .build();
    }
}
