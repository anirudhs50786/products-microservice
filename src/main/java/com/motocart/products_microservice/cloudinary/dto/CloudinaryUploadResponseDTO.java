package com.motocart.products_microservice.cloudinary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudinaryUploadResponseDTO {
    private String imageUrl;
    private String imageId;      // public_id
    private String originalFileName;
    private boolean success;
    private String errorMessage;  // if upload failed
}