package com.example.transfer_log_demo_service_consumer.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class TransferRequest {
    private String transferId;
    private String sender;
    private String receiver;
    private Double amount;
    private String currency;
    private Instant timestamp;
}
