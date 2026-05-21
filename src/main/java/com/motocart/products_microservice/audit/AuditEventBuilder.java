package com.motocart.products_microservice.audit;

import com.motocart.library.common.event.AuditEvent;
import com.motocart.library.common.types.AuditEntityType;
import com.motocart.library.security.AuthHelper;
import com.motocart.products_microservice.product.entity.ProductEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuditEventBuilder {

    private static final String SERVICE_NAME = "PRODUCTS-MICROSERVICE";

    private final AuditEventProducer auditEventProducer;

    public AuditEventBuilder(AuditEventProducer auditEventProducer) {
        this.auditEventProducer = auditEventProducer;
    }

    @Async("productExecutor")
    public void publishAddProductAuditEvent(ProductEntity product) {
        Map<String, AuditEvent.FieldChangePair> changedFields = new HashMap<>();
        AuditEvent.addChange(changedFields, "Product Name", null, product.getProductName());
        AuditEvent.addChange(changedFields, "Product Code", null, product.getProductCode());
        AuditEvent.addChange(changedFields, "Product Description", null, product.getProductDescription());
        AuditEvent.addChange(changedFields, "Is Archived", null, product.isArchived());
        AuditEvent.addChange(changedFields, "Firm Name", null, product.getFirmName());
        AuditEvent.addChange(changedFields, "Category Id", null, product.getCategory().getCategoryId());

        auditEventProducer.publishAuditEvent(AuditEvent.builder()
                .auditLogId(UUID.randomUUID().toString())
                .entityId(product.getProductId())
                .entityType(AuditEntityType.PRODUCT)
                .action("PRODUCT_CREATED")
                .changedFieldsPairMap(changedFields)
                .userId(AuthHelper.getAuthUserId())
                .sourceService(SERVICE_NAME)
                .timeStamp(Instant.now())
                .build());
    }

    @Async("productExecutor")
    public void publishUpdateProductAuditEvent(ProductEntity productOriginal, ProductEntity product) {
        Map<String, AuditEvent.FieldChangePair> changedFields = new HashMap<>();
        AuditEvent.addChange(changedFields, "Product Name", productOriginal.getProductName(), product.getProductName());
        AuditEvent.addChange(changedFields, "Product Code", productOriginal.getProductCode(), product.getProductCode());
        AuditEvent.addChange(changedFields, "Product Description", productOriginal.getProductDescription(), product.getProductDescription());
        AuditEvent.addChange(changedFields, "Is Archived", productOriginal.isArchived(), product.isArchived());
        AuditEvent.addChange(changedFields, "Firm Name", productOriginal.getFirmName(), product.getFirmName());
        AuditEvent.addChange(changedFields, "Category Id", productOriginal.getCategory().getCategoryId(), product.getCategory().getCategoryId());

        auditEventProducer.publishAuditEvent(AuditEvent.builder()
                .auditLogId(UUID.randomUUID().toString())
                .entityId(product.getProductId())
                .entityType(AuditEntityType.PRODUCT)
                .action("PRODUCT_CREATED")
                .changedFieldsPairMap(changedFields)
                .userId(AuthHelper.getAuthUserId())
                .sourceService(SERVICE_NAME)
                .timeStamp(Instant.now())
                .build());
    }
}
