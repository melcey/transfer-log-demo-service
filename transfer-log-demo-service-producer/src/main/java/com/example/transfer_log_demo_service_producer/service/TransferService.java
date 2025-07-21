package com.example.transfer_log_demo_service_producer.service;

import com.example.transfer_log_demo_service_producer.dto.TransferRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

// Bu servis, periyodik olarak sahte transfer verileri üretip Kafka'ya gönderir.
// Aynı zamanda hata durumlarını simüle etmek için de kullanılır.
@Service
@Slf4j
public class TransferService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TaskScheduler taskScheduler;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    // application.properties dosyasından alınan değerler.
    private final String topicName;
    private final double errorChance;
    private final int minDelayMs;
    private final int maxDelayMs;

    private final String[] currencies = { "USD", "EUR", "GBP", "JPY", "TRY" };
    private final String[] names = { "Melike", "Ceyda", "Ömer", "Faruk", "Selenay", "Esra", "Hümeyra", "Muammer",
            "Furkan" };

    public TransferService(KafkaTemplate<String, String> kafkaTemplate,
            TaskScheduler taskScheduler,
            @Value("${transfer.topic}") String topicName,
            @Value("${transfer.error.chance}") double errorChance,
            @Value("${transfer.scheduler.min-delay-ms}") int minDelayMs,
            @Value("${transfer.scheduler.max-delay-ms}") int maxDelayMs) {
        this.kafkaTemplate = kafkaTemplate;
        this.taskScheduler = taskScheduler;
        this.topicName = topicName;
        this.errorChance = errorChance;
        this.minDelayMs = minDelayMs;
        this.maxDelayMs = maxDelayMs;
        // ObjectMapper'ı Java 8 zaman tiplerini (örn: Instant) işleyecek şekilde
        // yapılandırır.
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @PostConstruct
    public void scheduleDummyTransfer() {
        scheduleNextRun();
    }

    private void scheduleNextRun() {
        long delay = ThreadLocalRandom.current().nextLong(minDelayMs, maxDelayMs);
        taskScheduler.schedule(() -> {
            generateAndSendDummyTransfer();
            scheduleNextRun();
        }, Instant.now().plusMillis(delay));
    }

    // Sahte transfer verisi veya hata mesajı oluşturur ve Kafka'ya gönderir.
    public void generateAndSendDummyTransfer() {
        String transferData;
        try {
            if (random.nextDouble() < errorChance) {
                transferData = "ERROR: Transfer process edilemedi: " + UUID.randomUUID().toString();
                log.warn("Bir hata mesajı oluşturuldu.");
            } else {
                TransferRequest transfer = createDummyTransfer();
                transferData = objectMapper.writeValueAsString(transfer);
                log.info("Sahte transfer verisi oluşturuldu: {}", transferData);
            }
            // Oluşturulan mesaj Kafka'ya gönderilir ve gönderme durumu (başarı/hata)
            // loglanır.
            kafkaTemplate.send(topicName, transferData).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Mesaj Kafka'ya gönderilemedi", ex);
                } else {
                    log.debug("Mesaj başarıyla topic'e gönderildi {}: {}", topicName, transferData);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Sahte transfer verisi JSON'a çevrilirken hata oluştu", e);
        }
    }

    // Rastgele değerlerle bir TransferRequest nesnesi oluşturur.
    private TransferRequest createDummyTransfer() {
        TransferRequest transfer = new TransferRequest();
        transfer.setTransferId(UUID.randomUUID().toString().substring(0, 8));
        transfer.setSender(names[random.nextInt(names.length)]);
        transfer.setReceiver(names[random.nextInt(names.length)]);
        transfer.setAmount(ThreadLocalRandom.current().nextDouble(10, 1000));
        transfer.setCurrency(currencies[random.nextInt(currencies.length)]);
        transfer.setTimestamp(Instant.now());
        return transfer;
    }
}