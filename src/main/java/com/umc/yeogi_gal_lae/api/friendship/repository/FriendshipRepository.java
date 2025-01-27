package com.umc.yeogi_gal_lae.api.friendship.repository;

import com.umc.yeogi_gal_lae.api.friendship.domain.FriendshipInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipInvite, Long> {
    Optional<FriendshipInvite> findByToken(String token);
}