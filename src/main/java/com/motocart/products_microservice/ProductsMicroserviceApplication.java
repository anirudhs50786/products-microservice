package com.motocart.products_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(scanBasePackages = "com.motocart")
@EnableDiscoveryClient
@EnableRetry
public class ProductsMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductsMicroserviceApplication.class, args);
    }

}
