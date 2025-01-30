package com.umc.yeogi_gal_lae.api.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String title;
    private String caption;
    private String type; // "VOTE", "COURSE", "BUDGET", etc.
}