package com.now_here5.now_here.global.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.now_here5.now_here.global.response.ResponseForm;

import com.now_here5.now_here.global.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401

        // response type과 인코딩 설정
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        PrintWriter writer = response.getWriter();
        ResponseForm unAuthenticatedError = ResponseForm.of(ResponseCode.AUTHENTICATION_FAIL);
        String json = new ObjectMapper().writeValueAsString(unAuthenticatedError);
        writer.write(json);
        writer.flush();
    }

}
