package com.example.transfer_log_demo_service_producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAutoConfiguration
@SpringBootApplication
public class TransferLogDemoServiceProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransferLogDemoServiceProducerApplication.class, args);
	}

}
