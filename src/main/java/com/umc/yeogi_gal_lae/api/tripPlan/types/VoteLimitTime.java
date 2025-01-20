package com.umc.yeogi_gal_lae.api.tripPlan.types;

import lombok.Getter;

@Getter
public enum VoteLimitTime {
    THIRTY_MINUTES("30분"),
    SIXTY_MINUTES("60분"),
    FOUR_HOURS("4시간"),
    SIX_HOURS("6시간");

    private final String description;

    VoteLimitTime(String description) {
        this.description = description;
    }

}