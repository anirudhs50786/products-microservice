package com.motocart.products_microservice.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.motocart.library.common.exception.GlobalException;
import org.springframework.lang.NonNull;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @NonNull
    @Retryable(retryFor = Exception.class, maxAttempts = 4, backoff = @Backoff(delay = 2000))
    public Map uploadFile(MultipartFile file) {
        try {
            return cloudinary.uploader().upload(file.getBytes(), null);
        } catch (Exception e) {
            throw new GlobalException("Failed to upload image to cloud.", e);
        }
    }

}
