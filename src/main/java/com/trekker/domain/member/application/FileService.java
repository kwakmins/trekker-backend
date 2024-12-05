package com.trekker.domain.member.application;

import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    // 업로드된 파일이 제공될 URL의 공통 경로 (리소스 핸들러와 매핑)
    private static final String FILE_PATH = "/uploads/profile-images/";
    // 허용 확장자
    private static final List<String> ALLOWED_FILE_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");

    // 파일 저장소의 실제 경로
    private final Path fileStorageLocation;

    public FileService(@Value("${file.dir}") String fileDir) {
        fileStorageLocation = Paths.get(fileDir)
                .toAbsolutePath()
                .normalize();
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception exception) {
            // 파일 저장 디렉토리를 생성할 수 없음
            throw new BusinessException(fileDir, "fileStorageLocation", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String saveProfileImage(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // 파일 이름 검증
            if (originalFileName.contains("..")) {
                throw new BusinessException(originalFileName, "fileName", ErrorCode.MEMBER_FILE_BAD_REQUEST);
            }

            // 파일 확장자 검증
            String fileExtension = StringUtils.getFilenameExtension(originalFileName).toLowerCase();
            if (!ALLOWED_FILE_EXTENSIONS.contains(fileExtension)) {
                throw new BusinessException(fileExtension, "fileExtension", ErrorCode.MEMBER_FILE_BAD_REQUEST);
            }

            // 고유한 파일 이름 생성
            String newFileName = UUID.randomUUID() + "." + fileExtension;

            // 파일 저장 경로
            Path targetLocation = fileStorageLocation.resolve(newFileName);

            // 파일 저장 (기존 파일이 있으면 덮어쓰기)
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return FILE_PATH + newFileName;

        } catch (IOException ex) {
            // 파일 저장 실패 시 예외 발생
            throw new BusinessException(originalFileName, "fileName", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 기존 프로필 이미지를 삭제합니다.
     *
     * @param fileName 삭제할 파일의 이름
     */
    public void deleteProfileImage(String fileName) {
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // 파일 삭제 실패 시 예외 발생
            throw new BusinessException(fileName, "fileName", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
