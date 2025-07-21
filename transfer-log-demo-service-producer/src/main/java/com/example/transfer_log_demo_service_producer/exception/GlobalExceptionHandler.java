package com.example.transfer_log_demo_service_producer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

// @ControllerAdvice anotasyonu, bu sınıfın tüm @Controller sınıfları için
// merkezi bir exception yönetimi yapmasını sağlar.
@ControllerAdvice
public class GlobalExceptionHandler {

    // @Valid anotasyonu ile yapılan doğrulama başarısız olduğunda fırlatılan
    // MethodArgumentNotValidException'ı yakalar.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // Hata listesindeki her bir alan için hata mesajını bir map'e ekler.
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        // Hataları ve 400 Bad Request durum kodunu içeren bir yanıt döner.
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}