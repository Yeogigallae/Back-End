package com.umc.yeogi_gal_lae.api.tripPlan.types;

import lombok.Getter;

@Getter
public enum Meal {
    RESTAURANT("레스토랑"),
    STREET_FOOD("스트릿푸드"),
    SELF_CATERING("자가식당");

    private final String description;

    Meal(String description) {
        this.description = description;
    }

}