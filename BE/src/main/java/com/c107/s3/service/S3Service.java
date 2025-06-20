package com.c107.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.c107.s3.entity.S3Entity;
import com.c107.s3.repository.S3Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;
    private final S3Repository s3Repository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${s3.prefix}")
    private String prefix;


    /**
     * S3에 파일 업로드 후 이미지 DB에 저장.
     * @param file 업로드할 파일
     * @param usageType 이미지 사용처 ("PROFILE", "CHALLENGE", "CARD" 등)
     * @param usageId 해당 사용처의 아이디 (예: 유저 아이디)
     * @return 업로드된 파일의 URL
     */
    public String uploadProfileImage(MultipartFile file, String usageType, Integer usageId) throws IOException {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new IllegalArgumentException("파일명이 존재하지 않습니다.");
        }

        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        String key = String.format("%s/%s/%d/%s", prefix, usageType.toLowerCase(), usageId, uniqueFileName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, file.getInputStream(), metadata);
        amazonS3Client.putObject(putObjectRequest);

        String encodedFileName = URLEncoder.encode(uniqueFileName, StandardCharsets.UTF_8);
        String encodedKey = String.format("%s/%s/%d/%s", prefix, usageType.toLowerCase(), usageId, encodedFileName);

        String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + encodedKey;

        S3Entity image = S3Entity.builder()
                .url(fileUrl)
                .extension(getExtension(originalFileName))
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .usageType(usageType)
                .usageId(usageId)
                .build();
        s3Repository.save(image);

        return fileUrl;
    }

    // 파일 확장자를 추출하는 헬퍼 메서드
    private String getExtension(String fileName) {
        if(fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }
}
