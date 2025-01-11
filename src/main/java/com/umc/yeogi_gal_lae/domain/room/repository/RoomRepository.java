package com.umc.yeogi_gal_lae.domain.room.repository;

import com.umc.yeogi_gal_lae.domain.room.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // Room 엔티티를 기반으로 필요한 추가 쿼리 메서드 정의 가능
}