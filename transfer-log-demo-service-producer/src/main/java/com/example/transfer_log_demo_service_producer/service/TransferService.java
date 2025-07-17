package com.example.transfer_log_demo_service_producer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.transfer_log_demo_service_producer.dto.TransferRequest;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public String sendTransfer(TransferRequest request) {
        String message = convertToJson(request);
        kafkaTemplate.send("transfer-topic", message);
        return "Sent transfer: " + message;
    }

    private String convertToJson(TransferRequest request) {
        // Implement JSON conversion
    }
}