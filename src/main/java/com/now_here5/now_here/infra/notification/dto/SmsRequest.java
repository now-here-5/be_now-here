package com.now_here5.now_here.infra.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SmsRequest {

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String message;

    @Builder
    public SmsRequest(String phoneNumber, String message) {
        this.phoneNumber = formatPhoneNumber(phoneNumber);
        this.message = message;
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return "+82" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }
}