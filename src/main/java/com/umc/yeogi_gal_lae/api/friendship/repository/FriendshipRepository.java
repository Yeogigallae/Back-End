package com.umc.yeogi_gal_lae.api.friendship.repository;

import com.umc.yeogi_gal_lae.api.friendship.domain.Friendship;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findById(Long id);
    List<Friendship> findByInviterIdOrInviteeId(Long inviterId, Long inviteeId);
    Optional<Friendship> findByInviterIdAndInviteeId(Long inviterId, Long inviteeId);

//     inviter invitee를 user 객체로 바로 조회할 일이 없으면 이 코드 필요 없는 거 아닌지
    @Query("SELECT f FROM Friendship f WHERE f.inviter.id = :userId")
    List<Friendship> findInvitedFriends(@Param("userId") Long userId);

    @Query("SELECT f FROM Friendship f WHERE f.invitee.id = :userId")
    List<Friendship> findReceivedFriends(@Param("userId") Long userId);



    //쓰면 Jpa로 이렇게 써도 될지
//    // 내가 초대한 친구 목록 조회 (inviter_id = userId)
//    List<Friendship> findByInviterId(Long inviterId);
//
//    // 내가 초대받은 친구 목록 조회 (invitee_id = userId)
//    List<Friendship> findByInviteeId(Long inviteeId);

}