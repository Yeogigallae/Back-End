package com.umc.yeogi_gal_lae.api.place.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class PlaceResponse {

    private Long placeId;
    private Long roomId;
    private String placeName;
    private String address;
    private Double lat;
    private Double lng;
}
