package com.trekker.global.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trekker.global.exception.dto.ErrorResDto;
import com.trekker.global.exception.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 권한 예외 핸들러
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException {

        log.warn("Access Denied: URI = {}, Method = {}, Message = {}",
                request.getRequestURI(),
                request.getMethod(),
                accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResDto errorResponse = ErrorResDto.of(ErrorCode.ACCESS_DENIED_EXCEPTION);

        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
