package com.joayong.skillswap.util;

import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.PostException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileUploadUtil {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.folder}")
    private String folderName;

    // S3에 이미지 업로드 후 URL 반환
    public String saveFile(MultipartFile file) {
        validateImages(file);
        String originalFilename = file.getOriginalFilename();
        String newFilename = UUID.randomUUID() + "_" + originalFilename;
        String fileKey = folderName + "/" + newFilename;

        try (InputStream inputStream = file.getInputStream()) {
            // S3에 파일 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, file.getSize()));

            log.info("File uploaded to S3: {}", fileKey);
            return "http://" + bucketName + ".s3.amazonaws.com/" + fileKey; // 업로드된 S3 URL 반환
        } catch (IOException e) {
            log.error("Failed to upload file to S3: {}", newFilename, e);
            throw new PostException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    private void validateImages(MultipartFile file) {
        if (file.isEmpty()) {
            throw new PostException(ErrorCode.INVALID_FILE_TYPE, "빈 파일입니다.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image")) {
            throw new PostException(ErrorCode.INVALID_FILE_TYPE, "이미지만 업로드 가능합니다.");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new PostException(ErrorCode.FILE_SIZE_EXCEEDED, "이미지 크기는 10MB를 초과할 수 없습니다.");
        }
    }
}
