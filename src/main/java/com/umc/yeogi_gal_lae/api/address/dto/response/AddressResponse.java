package com.umc.yeogi_gal_lae.api.address.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class AddressResponse {

    private Long id;
    private String address;
    private Double lat;
    private Double lng;
}
