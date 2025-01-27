package com.umc.yeogi_gal_lae.api.userImage.repository;

import com.umc.yeogi_gal_lae.api.userImage.domain.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {
    Optional<UserImage> findByUserId(Long userId);
}
