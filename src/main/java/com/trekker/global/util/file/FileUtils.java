package com.trekker.global.util.file;

import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 처리 관련 유틸리티 클래스
 */
public class FileUtils {

    // 허용 확장자 목록
    private static final List<String> ALLOWED_FILE_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");

    /**
     * 고유한 파일 이름을 생성합니다.
     *
     * @param originalFileName 원본 파일 이름
     * @return 고유한 파일 이름
     */
    public static String generateUniqueFileName(String originalFileName) {
        String fileExtension = StringUtils.getFilenameExtension(originalFileName);
        return UUID.randomUUID() + "." + fileExtension;
    }

    /**
     * 파일을 저장합니다.
     *
     * @param targetPath 저장할 파일의 경로
     * @param file 업로드된 MultipartFile
     * @throws IOException 파일 저장 중 예외 발생 시
     */
    public static void saveFile(Path targetPath, MultipartFile file) throws IOException {
        Files.copy(file.getInputStream(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 파일을 삭제합니다.
     *
     * @param targetPath 삭제할 파일의 경로
     * @throws IOException 파일 삭제 중 예외 발생 시
     */
    public static void deleteFile(Path targetPath) throws IOException {
        Files.deleteIfExists(targetPath);
    }

    /**
     * 파일 확장자를 검증합니다.
     *
     * @param fileName 검증할 파일 이름
     * @throws BusinessException 허용되지 않은 확장자일 경우 예외 발생
     */
    public static void validateFileExtension(String fileName) {
        String fileExtension = StringUtils.getFilenameExtension(fileName).toLowerCase();
        if (!ALLOWED_FILE_EXTENSIONS.contains(fileExtension)) {
            throw new BusinessException(fileExtension, "fileExtension", ErrorCode.MEMBER_FILE_BAD_REQUEST);
        }
    }

    /**
     * 파일 이름을 검증합니다.
     *
     * @param fileName 검증할 파일 이름
     * @throws BusinessException 파일 이름에 ".."이 포함되어 있을 경우 예외 발생
     */
    public static void validateFileName(String fileName) {
        if (fileName.contains("..")) {
            throw new BusinessException(fileName, "fileName", ErrorCode.MEMBER_FILE_BAD_REQUEST);
        }
    }
}