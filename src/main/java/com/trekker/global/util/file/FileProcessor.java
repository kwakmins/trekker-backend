package com.trekker.global.util.file;

import java.io.IOException;
import java.nio.file.Path;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 처리를 위한 함수형 인터페이스
 */
@FunctionalInterface
public interface FileProcessor {
    /**
     * 파일을 처리하는 메소드
     *
     * @param targetPath 처리할 파일의 경로
     * @param file 처리할 MultipartFile
     */
    void process(Path targetPath, MultipartFile file) throws IOException;
}