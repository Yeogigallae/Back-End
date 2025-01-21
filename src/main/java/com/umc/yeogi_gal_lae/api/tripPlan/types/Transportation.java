package com.umc.yeogi_gal_lae.api.tripPlan.types;

import lombok.Getter;

@Getter
public enum Transportation {
    CAR("자동차"),
    PUBLIC_TRANSPORT("대중교통"),
    RENTAL_CAR("렌트카");

    private final String description;

    Transportation(String description) {
        this.description = description;
    }

}