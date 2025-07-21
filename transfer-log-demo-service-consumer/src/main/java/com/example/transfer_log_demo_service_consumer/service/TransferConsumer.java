package com.example.transfer_log_demo_service_consumer.service;

import com.example.transfer_log_demo_service_consumer.dto.TransferRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

// Bu sınıf, Kafka topic'lerini dinler ve gelen mesajları işler.
@Slf4j
@Service
public class TransferConsumer {

    private final ObjectMapper objectMapper;

    public TransferConsumer() {
        // ObjectMapper'ı Java 8 zaman tiplerini (örn: Instant) işleyecek şekilde
        // yapılandırır.
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @KafkaListener(topics = "${transfer.topic}", groupId = "transfer-group")
    public void consumeTransfer(String message) {

        if (message.contains("ERROR:")) {
            log.error("bir hata mesajı alındı: {}", message);
            return;
        }

        try {
            // Gelen JSON mesajı TransferRequest nesnesine çevrilir (deserialize).
            TransferRequest transfer = objectMapper.readValue(message, TransferRequest.class);
            log.info("Başarıyla deserialize edilen transfer: {}", transfer);

        } catch (JsonProcessingException e) {
            log.error("Mesaj TransferRequest nesnesine çevrilemedi. Raw mesaj: {}", message, e);
        }
    }
}