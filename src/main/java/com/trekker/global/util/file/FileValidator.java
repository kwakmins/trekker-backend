package com.trekker.global.util.file;

import java.io.IOException;

/**
 * 파일 검증을 위한 함수형 인터페이스
 */
@FunctionalInterface
public interface FileValidator {
    /**
     * 파일을 검증하는 메소드
     *
     * @param fileName 검증할 파일 이름
     */
    void validate(String fileName);
}