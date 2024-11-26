package com.trekker.global.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trekker.global.exception.dto.ErrorResDto;
import com.trekker.global.exception.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인가 예외 핸들러
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        log.warn("Authentication Failed: URI = {}, Method = {}, Message = {}",
                request.getRequestURI(),
                request.getMethod(),
                authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResDto errorResponse = ErrorResDto.of(ErrorCode.ACCESS_AUTH_ENTRY_EXCEPTION);

        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
