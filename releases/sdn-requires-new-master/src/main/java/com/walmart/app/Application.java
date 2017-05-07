package com.walmart.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by wal-mart on 5/16/16.
 *
 * @author: wal-mart
 * @author: grosal3
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.walmart.app")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
