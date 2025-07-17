package com.example.transfer_log_demo_service_producer.controller;

import com.example.transfer_log_demo_service_producer.dto.TransferRequest;
import com.example.transfer_log_demo_service_producer.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @PostMapping
    public String createTransfer(@RequestBody TransferRequest request) {
        return transferService.sendTransfer(request);
    }
}