package com.trekker.global.config.exception;

import com.trekker.global.config.exception.custom.BusinessException;
import com.trekker.global.config.exception.dto.ErrorResDto;
import com.trekker.global.config.exception.enums.ErrorCode;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResDto> handleBusinessException(BusinessException e) {

        String errorMessage = formatErrorMessage(e.getInvalidValue(), e.getFieldName(), e.getMessage());
        logError(e, errorMessage);

        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(ErrorResDto.of(e.getErrorCode()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResDto> handleBindException(BindException e) {

        String errorMessage = formatErrorMessage(e);
        logError(e, errorMessage);

        return ResponseEntity.badRequest()
                .body(ErrorResDto.of(ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResDto> handleException(Exception e) {

        logError(e, "[EXCEPTION]  " + e.getClass());

        return ResponseEntity.internalServerError()
                .body(ErrorResDto.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    private void logError(Exception e, String errorMessage) {
        log.error(errorMessage);
        log.error("[EXCEPTION]  ERROR   -->   ", e);
    }


    private String formatErrorMessage(BindException e) {
        BindingResult bindingResult = e.getBindingResult();

        return bindingResult.getFieldErrors().stream()
                .map(fieldError ->
                        formatErrorMessage(
                                String.valueOf(fieldError.getRejectedValue()), // 값
                                fieldError.getField(), // 필드명
                                fieldError.getDefaultMessage() // 오류 메시지
                        )
                )
                .collect(Collectors.joining(", "));
    }

    private String formatErrorMessage(String invalidValue, String errorField, String errorMessage) {
        return String.format("[EXCEPTION]  ERROR_MESSAGE -->  [%s] %s: %s", invalidValue, errorField, errorMessage);
    }
}
