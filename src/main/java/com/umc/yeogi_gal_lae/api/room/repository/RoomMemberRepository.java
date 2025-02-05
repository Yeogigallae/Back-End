package com.umc.yeogi_gal_lae.api.room.repository;

import com.umc.yeogi_gal_lae.api.room.domain.RoomMember;
import com.umc.yeogi_gal_lae.api.room.domain.RoomMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomMemberRepository extends JpaRepository<RoomMember, RoomMemberId> {
    List<RoomMember> findAllByRoomId(Long roomId);//?
    List<RoomMember> findAllByUserId(Long userId);
}