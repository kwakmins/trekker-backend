package com.trekker.global.exception.dto;

import com.trekker.global.exception.enums.ErrorCode;
import lombok.Builder;

@Builder
public record ErrorResDto(
        Integer status,
        String message
) {

    public static ErrorResDto of(ErrorCode errorCode) {
        return ErrorResDto.builder()
                .status(errorCode.getHttpStatus().value())
                .message(errorCode.getMessage())
                .build();
    }
}
