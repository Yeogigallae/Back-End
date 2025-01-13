package com.umc.yeogi_gal_lae.api.address.repository;

import com.umc.yeogi_gal_lae.api.address.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
