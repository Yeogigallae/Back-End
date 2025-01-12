package com.umc.yeogi_gal_lae.domain.room.repository;

import com.umc.yeogi_gal_lae.domain.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.domain.room.domain.RoomMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomMemberRepository extends JpaRepository<RoomMember, RoomMemberId> {
    List<RoomMember> findAllByIdRoomId(Long roomId);
    List<RoomMember> findAllByIdUserId(Long userId);
}