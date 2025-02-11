package com.twilight.twilight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EntityScan(basePackages = "com.twilight.twilight.Model")
public class TwilightApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwilightApplication.class, args);
    }

}
