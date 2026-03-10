package com.sparta._9haejodelivery.service;

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
        // TODO: BusinessException으로 수정 필요
        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
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
        // TODO: BusinessException으로 수정 필요
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                    "허용되지 않은 파일 형식입니다."
            );
        }

        multipartFile.transferTo(new File(fullPath));

        return "/images/" + storeFilename;
    }

    // TODO: BusinessException으로 수정 필요
    private String extractExtension(String originalFilename) {
        if(originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }

        // 경로 정규화
        String normalizedFilename = Paths.get(originalFilename).getFileName().toString();
        return normalizedFilename.substring(normalizedFilename.lastIndexOf("."));
    }
}

