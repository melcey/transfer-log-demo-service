package com.example.transfer_log_demo_service_consumer.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private String transferId;
    private String sender;
    private String receiver;
    private double amount;
    private String currency;
}
