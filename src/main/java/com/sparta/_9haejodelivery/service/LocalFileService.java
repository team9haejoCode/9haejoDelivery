package com.sparta._9haejodelivery.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class LocalFileService {
    private final String fileDir = System.getProperty("user.dir") + "/uploads/";

    public String saveFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        File directory = new File(fileDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        String fullPath = fileDir + storeFilename;

        multipartFile.transferTo(new File(fullPath));

        return "/uploads/" + storeFilename;
    }
}

