package com.example.transfer_log_demo_service_consumer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransferConsumer {

    @KafkaListener(topics = "transfer-topic", groupId = "transfer-group")
    public void consumeTransfer(String message) {
        if (message.contains("ERROR")) {
            log.error("Error processing: {}", message);
        } else {
            log.info("Received transfer: {}", message);
            // Process the transfer
        }
    }
}