// Bu sınıf, transfer verilerini Kafka'ya göndermek için bir REST API sunar.
package com.example.transfer_log_demo_service_producer.controller;

import com.example.transfer_log_demo_service_producer.dto.TransferRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topicName;

    public TransferController(KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${transfer.topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topicName = topicName;
    }

    // Gelen transfer verisini alır, JSON'a çevirir ve Kafka'ya gönderir.
    @PostMapping
    public ResponseEntity<String> sendTransfer(@Valid @RequestBody TransferRequest transferRequest) {
        try {
            String transferData = objectMapper.writeValueAsString(transferRequest);
            kafkaTemplate.send(topicName, transferData);
            log.info("Transfer verisi Kafka'ya gönderildi: {}", transferData);
            return ResponseEntity.ok("Transfer verisi Kafka'ya gönderildi: " + transferData);
        } catch (JsonProcessingException e) {
            log.error("Transfer isteği JSON'a çevrilirken hata oluştu", e);
            return ResponseEntity.status(500).body("İstek işlenirken hata oluştu.");
        }
    }
}