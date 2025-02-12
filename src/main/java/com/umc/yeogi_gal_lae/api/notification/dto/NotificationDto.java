package com.umc.yeogi_gal_lae.api.notification.dto;

import com.umc.yeogi_gal_lae.api.tripPlan.types.TripPlanType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String title;
    private String caption;
    private String type; // "VOTE", "COURSE", "BUDGET", etc.
    private Long tripPlanId;
    private TripPlanType tripPlanType;
}