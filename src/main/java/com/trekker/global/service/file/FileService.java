package com.trekker.global.service.file;

import com.trekker.global.util.file.FileProcessor;
import com.trekker.global.util.file.FileUtils;
import com.trekker.global.util.file.FileValidator;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
public class FileService {

    // 업로드된 파일이 제공될 URL의 공통 경로 (리소스 핸들러와 매핑)
    private static final String FILE_PATH = "/uploads/profile-images/";

    // 파일 저장소의 실제 경로
    private final Path fileStorageLocation;

    public FileService(@Value("${file.dir}") String fileDir) {
        this.fileStorageLocation = Paths.get(fileDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (IOException exception) {
            // 파일 저장 디렉토리를 생성할 수 없음
            throw new BusinessException(fileDir, "fileStorageLocation",
                    ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 프로필 이미지를 저장합니다.
     *
     * @param file 업로드된 파일
     * @return 저장된 파일의 URL 경로
     */
    public String saveProfileImage(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        // 파일 검증을 위한 FileValidator 정의
        FileValidator fileNameValidator = FileUtils::validateFileName;
        FileValidator fileExtensionValidator = FileUtils::validateFileExtension;

        // 파일 검증 실행
        fileNameValidator.validate(originalFileName);
        fileExtensionValidator.validate(originalFileName);

        // 고유한 파일 이름 생성
        String newFileName = FileUtils.generateUniqueFileName(originalFileName);
        Path targetLocation = fileStorageLocation.resolve(newFileName);

        // 파일 저장을 위한 FileProcessor 정의
        FileProcessor fileSaver = FileUtils::saveFile;

        try {
            // 파일 저장 실행
            fileSaver.process(targetLocation, file);
            return FILE_PATH + newFileName;
        } catch (IOException ex) {
            // 파일 저장 실패 시 예외 발생
            throw new BusinessException(originalFileName, "fileName",
                    ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 프로필 이미지를 삭제합니다.
     *
     * @param existingImage 삭제할 파일의 URL 경로
     */
    public void deleteProfileImage(String existingImage) {
        String fileName = extractFileName(existingImage);
        Path filePath = fileStorageLocation.resolve(fileName).normalize();

        // 파일 삭제를 위한 FileProcessor 정의
        FileProcessor fileDeleter = (path, file) -> FileUtils.deleteFile(path);

        try {
            // 파일 삭제 실행
            fileDeleter.process(filePath, null);
        } catch (IOException ex) {
            // 파일 삭제 실패 시 예외 발생
            throw new BusinessException(existingImage, "fileName", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 이미지 경로에서 파일명을 추출합니다.
     *
     * @param imagePath 이미지 경로 (예: "/uploads/profile-images/image.png")
     * @return 추출된 파일명 (예: "image.png")
     */
    private String extractFileName(String imagePath) {
        return imagePath.substring(imagePath.lastIndexOf("/") + 1);
    }
}