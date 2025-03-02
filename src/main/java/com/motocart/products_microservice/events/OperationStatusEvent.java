package com.motocart.products_microservice.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OperationStatusEvent {
    private String status;
    private String eventType;
    private String errorDetails;
    private String correlationId;
}
