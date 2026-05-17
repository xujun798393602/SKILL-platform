package com.skill.platform.common.service;

import com.skill.platform.common.exception.FileStorageException;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    public FileStorageService(MinioClient minioClient, String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @PostConstruct
    public void init() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());

            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
                log.info("Created bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to initialize MinIO bucket", e);
        }
    }

    public String uploadFile(InputStream inputStream, String objectName, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, -1, 10485760) // 10MB part size
                .contentType(contentType)
                .build());

            log.info("Uploaded file: {}", objectName);
            return objectName;
        } catch (Exception e) {
            log.error("Failed to upload file: {}", objectName, e);
            throw new FileStorageException("Failed to upload file", e);
        }
    }

    public InputStream downloadFile(String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
        } catch (Exception e) {
            log.error("Failed to download file: {}", objectName, e);
            throw new FileStorageException("Failed to download file", e);
        }
    }

    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
            log.info("Deleted file: {}", objectName);
        } catch (Exception e) {
            log.error("Failed to delete file: {}", objectName, e);
            throw new FileStorageException("Failed to delete file", e);
        }
    }

    public String getPresignedUrl(String objectName, int expirySeconds) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET)
                .expiry(expirySeconds)
                .build());
        } catch (Exception e) {
            log.error("Failed to generate presigned URL: {}", objectName, e);
            throw new FileStorageException("Failed to generate presigned URL", e);
        }
    }

    public String buildObjectPath(UUID skillId, String version, String fileName) {
        return String.format("skills/%s/%s/%s", skillId, version, fileName);
    }
}
