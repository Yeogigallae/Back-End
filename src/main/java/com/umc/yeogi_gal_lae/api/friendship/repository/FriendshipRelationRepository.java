package com.umc.yeogi_gal_lae.api.friendship.repository;

import com.umc.yeogi_gal_lae.api.friendship.domain.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRelationRepository extends JpaRepository<Friendship, Long> {
}