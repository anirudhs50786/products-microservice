package com.motocart.products_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.motocart")
public class ProductsMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductsMicroserviceApplication.class, args);
    }

}
