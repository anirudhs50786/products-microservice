package com.motocart.products_microservice.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.motocart.library.common.exception.GlobalException;
import com.motocart.products_microservice.cloudinary.dto.CloudinaryUploadResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Value("${cloudinary.upload.max-files:10}")
    private int maxFiles;

    @NonNull
    @Retryable(retryFor = Exception.class, maxAttempts = 4, backoff = @Backoff(delay = 2000))
    public Map uploadFile(MultipartFile file) {
        try {
            return cloudinary.uploader().upload(file.getBytes(), null);
        } catch (Exception e) {
            throw new GlobalException("Failed to upload image to cloud.", e);
        }
    }

    public List<CloudinaryUploadResponseDTO> uploadFilesInParallel(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return List.of();
        }

        MultipartFile[] filesToProcess = files;
        if (files.length > maxFiles) {
            log.warn("Attempting to upload {} files, but max is {}. Will process only first {}",
                    files.length, maxFiles, maxFiles);
            filesToProcess = Arrays.copyOf(files, maxFiles);
        }

        List<CompletableFuture<CloudinaryUploadResponseDTO>> uploadFutures = new ArrayList<>();

        for (MultipartFile file : filesToProcess) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            CompletableFuture<CloudinaryUploadResponseDTO> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Map response = uploadFile(file);
                    return CloudinaryUploadResponseDTO.builder()
                            .imageUrl(response.get("url").toString())
                            .imageId(response.get("public_id").toString())
                            .originalFileName(file.getOriginalFilename())
                            .success(true)
                            .build();
                } catch (Exception e) {
                    log.error("Failed to upload file {}: {}", file.getOriginalFilename(), e.getMessage());
                    return CloudinaryUploadResponseDTO.builder()
                            .originalFileName(file.getOriginalFilename())
                            .success(false)
                            .errorMessage(e.getMessage())
                            .build();
                }
            }, executorService);

            uploadFutures.add(future);
        }

        // Wait for all uploads to complete
        CompletableFuture.allOf(uploadFutures.toArray(new CompletableFuture[0])).join();

        // Collect results
        return uploadFutures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

}
