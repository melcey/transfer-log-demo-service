package com.example.transfer_log_demo_service_producer.controller;

import com.example.transfer_log_demo_service_producer.dto.TransferRequest;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    private final KafkaTemplate<String, String> kafkaTemplate;

    // No @Value needed - Spring will auto-use the default topic
    public TransferController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public String sendTransfer(@RequestBody TransferRequest transferRequest) {
        String transferData = convertTransferToJson(transferRequest);
        kafkaTemplate.sendDefault(transferData); // Uses the default topic
        return "Transfer data sent to Kafka: " + transferData;
    }

    private String convertTransferToJson(TransferRequest transfer) {
        // Simple JSON conversion (in a real project, use Jackson/ObjectMapper)
        return String.format(
                "{\"transferId\":\"%s\",\"sender\":\"%s\",\"receiver\":\"%s\",\"amount\":%.2f,\"currency\":\"%s\",\"timestamp\":\"%s\"}",
                transfer.getTransferId(),
                transfer.getSender(),
                transfer.getReceiver(),
                transfer.getAmount(),
                transfer.getCurrency(),
                transfer.getTimestamp());
    }
}