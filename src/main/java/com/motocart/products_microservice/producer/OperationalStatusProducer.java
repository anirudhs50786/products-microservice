package com.motocart.products_microservice.producer;

import com.motocart.products_microservice.events.OperationStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OperationalStatusProducer {
    private final KafkaTemplate<String, OperationStatusEvent> statusKafkaTemplate;

    public OperationalStatusProducer(KafkaTemplate<String, OperationStatusEvent> kafkaTemplate) {
        this.statusKafkaTemplate = kafkaTemplate;
    }

    public void sendStatus(OperationStatusEvent statusEvent) {
        log.info("sending status to proxy server");
        statusKafkaTemplate.send("operation-status-events", statusEvent);
    }

}
