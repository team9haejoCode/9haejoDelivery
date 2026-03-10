package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.common.BusinessException;
import com.sparta._9haejodelivery.common.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class LocalFileService {
    private final String fileDir = System.getProperty("user.dir") + "/uploads/";

    // 파일 확장자 지정
    private static final List<String> ALLOWED_EXTENSIONS =
            Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp");

    // 파일 크기 제한 : 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public String saveFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        // 파일 크기 검증
        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        File directory = new File(fileDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        String storeFilename = UUID.randomUUID().toString() + extension;
        String fullPath = fileDir + storeFilename;

        // 파일 확장자 검증
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException(ErrorCode.INVALID_FILE_EXTENSION);
        }

        multipartFile.transferTo(new File(fullPath));

        return "/images/" + storeFilename;
    }

    private String extractExtension(String originalFilename) {
        if(originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException(ErrorCode.INVALID_FILE_NAME);
        }

        // 경로 정규화
        String normalizedFilename = Paths.get(originalFilename).getFileName().toString();
        return normalizedFilename.substring(normalizedFilename.lastIndexOf("."));
    }
}

