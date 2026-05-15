package com.motocart.products_microservice.audit;

import com.motocart.library.common.event.AuditEvent;
import com.motocart.library.kafka.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuditEventProducer {

    private final KafkaTemplate<String, AuditEvent> kafkaTemplate;

    public AuditEventProducer(KafkaTemplate<String, AuditEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishAuditEvent(AuditEvent auditEvent) {
        kafkaTemplate.send(KafkaTopics.AUDIT_EVENTS, auditEvent);
    }
}
