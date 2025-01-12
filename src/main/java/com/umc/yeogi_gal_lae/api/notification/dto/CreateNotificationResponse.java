package com.umc.yeogi_gal_lae.api.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateNotificationResponse {
    private Long id;
    private String message;
}