package com.example.transfer_log_demo_service_producer.service;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransferService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Random random = new Random();
    private final String[] currencies = { "USD", "EUR", "GBP", "JPY", "TRY" };
    private final String[] names = { "Melike", "Ceyda", "Ömer", "Faruk", "Selenay", "Esra", "Hümeyra", "Muammer",
            "Furkan" };

    public TransferService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Autowired
    private TaskScheduler taskScheduler;

    @PostConstruct
    public void scheduleDummyTransfer() {
        scheduleNextRun();
    }

    private void scheduleNextRun() {
        int delay = random.nextInt(10000); // Random delay 0-10 seconds
        taskScheduler.schedule(() -> {
            generateAndSendDummyTransfer();
            scheduleNextRun(); // Schedule the next run
        }, Instant.now().plusMillis(delay));
    }

    public void generateAndSendDummyTransfer() {

        boolean isError = random.nextInt(10) == 0; // 10% chance of error

        String transferData;
        if (isError) {
            transferData = "ERROR: Failed to process transfer " + UUID.randomUUID().toString();
        } else {
            // Generate random transfer data
            String transferId = UUID.randomUUID().toString().substring(0, 8);
            String sender = names[random.nextInt(names.length)];
            String receiver = names[random.nextInt(names.length)];
            double amount = 10 + (1000 - 10) * random.nextDouble();
            String currency = currencies[random.nextInt(currencies.length)];
            String timestamp = Instant.now().toString();

            // Create JSON string
            transferData = String.format(
                    "{\"transferId\":\"%s\",\"sender\":\"%s\",\"receiver\":\"%s\"," +
                            "\"amount\":%.2f,\"currency\":\"%s\",\"timestamp\":\"%s\"}",
                    transferId, sender, receiver, amount, currency, timestamp);
        }

        // Send to Kafka
        kafkaTemplate.send("transfer-topic", transferData);
        log.debug("Generated transfer: {}", transferData);
    }
}