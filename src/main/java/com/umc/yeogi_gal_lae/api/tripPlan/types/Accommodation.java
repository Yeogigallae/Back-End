package com.umc.yeogi_gal_lae.api.tripPlan.types;

import lombok.Getter;

@Getter
public enum Accommodation {
    HOTEL("호텔"),
    HOSTEL("호스텔"),
    RESORT("리조트");

    private final String description;

    Accommodation(String description) {
        this.description = description;
    }

}