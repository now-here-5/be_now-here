package com.now_here5.now_here.global.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.now_here5.now_here.global.response.ResponseForm;
import com.now_here5.now_here.global.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                           AccessDeniedException accessDeniedException) throws IOException {

            // 권한이 없을 때 403

            // response type과 인코딩 설정
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            PrintWriter writer = response.getWriter();
            ResponseForm unAuthorizedError = ResponseForm.of(ResponseCode.AUTHORIZATION_FAIL);
            String json = new ObjectMapper().writeValueAsString(unAuthorizedError);
            writer.write(json);
            writer.flush();
        }
}
