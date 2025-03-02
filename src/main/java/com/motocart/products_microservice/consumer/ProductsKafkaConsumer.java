package com.motocart.products_microservice.consumer;

import com.motocart.products_microservice.events.OperationStatusEvent;
import com.motocart.products_microservice.events.ProductsEvent;
import com.motocart.products_microservice.producer.OperationalStatusProducer;
import com.motocart.products_microservice.service.ProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductsKafkaConsumer {

    private final ProductsService productsService;
    private final OperationalStatusProducer statusProducer;

    public ProductsKafkaConsumer(ProductsService productsService, OperationalStatusProducer statusProducer) {
        this.productsService = productsService;
        this.statusProducer = statusProducer;
    }

    @KafkaListener(topics = "products-events", groupId = "products-group")
    public void consumeUserEvent(ProductsEvent productsEvent) {
        OperationStatusEvent statusEvent = null;
        try {
            log.info("Received Kafka event: {}", productsEvent.getEventType());
            if ("INSERT".equals(productsEvent.getEventType())) {
                productsService.addProduct(productsEvent.getProductsDTO());
            } else if ("UPDATE".equals(productsEvent.getEventType())) {
                productsService.updateProduct(productsEvent.getProductsDTO());
            } else {
                log.error("Unknown event type received: {}", productsEvent.getEventType());
            }
            statusEvent = OperationStatusEvent.builder().eventType(productsEvent.getEventType()).status("SUCCESS").correlationId(productsEvent.getCorrelationId()).build();
        } catch (Exception exception) {
            statusEvent = OperationStatusEvent.builder().eventType(productsEvent.getEventType()).status("FAILED").correlationId(productsEvent.getCorrelationId()).errorDetails(exception.getMessage()).build();
            log.error("Product update event failed");
        } finally {
            statusProducer.sendStatus(statusEvent);
        }
    }
}
