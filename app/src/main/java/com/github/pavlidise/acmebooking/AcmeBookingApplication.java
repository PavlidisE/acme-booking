package com.github.pavlidise.acmebooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class AcmeBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcmeBookingApplication.class, args);
    }

}
