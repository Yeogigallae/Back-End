package com.umc.yeogi_gal_lae.api.address.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressRequest {
    @NotBlank(message = "주소는 필수 입력값입니다.")
    private String address;
}
