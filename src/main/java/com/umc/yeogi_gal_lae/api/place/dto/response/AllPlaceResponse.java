package com.umc.yeogi_gal_lae.api.place.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@ToString
public class AllPlaceResponse {
    private final List<PlaceResponse> placeResponses;
}
