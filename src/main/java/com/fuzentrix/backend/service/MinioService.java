package com.fuzentrix.backend.service;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * Verifies the configured bucket exists at startup, creating it if absent.
     * Running this once at application startup avoids repeating the check on
     * every upload invocation.
     */
    @PostConstruct
    public void initBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Created MinIO bucket: {}", bucketName);
            } else {
                log.info("MinIO bucket '{}' already exists.", bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to initialize MinIO bucket '{}' at startup", bucketName, e);
            throw new IllegalStateException("MinIO bucket initialization failed", e);
        }
    }

    /**
     * Upload an object to a specific folder path within the single configured bucket.
     * Use this to separate files by course.
     * Example folder paths: "courses/course-101", "courses/course-101/thumbnails"
     */
    public String uploadFile(String folderPath, String filename, MultipartFile file) {
        try {
            // Sanitize folder path (remove trailing slash if any)
            if (folderPath.endsWith("/")) {
                folderPath = folderPath.substring(0, folderPath.length() - 1);
            }

            // Format object name to achieve separation
            String objectName = String.format("%s/%s", folderPath, filename);

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }
            log.info("File uploaded successfully to: {}/{}", bucketName, objectName);
            return objectName;
        } catch (Exception e) {
            log.error("Error occurred while uploading file to MinIO", e);
            throw new RuntimeException("Error occurred while uploading file to MinIO", e);
        }
    }

    /**
     * Generate a Pre-signed GET URL for securely downloading/streaming a file.
     */
    public String getPresignedUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(30, TimeUnit.MINUTES) // URL expires in 30 minutes
                            .build()
            );
        } catch (Exception e) {
            log.error("Error occurred while generating a pre-signed URL for MinIO object: {}", objectName, e);
            throw new RuntimeException("Error occurred while generating a pre-signed URL", e);
        }
    }
}
