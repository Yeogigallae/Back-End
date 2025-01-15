package com.umc.yeogi_gal_lae.api.tripPlan.repository;

import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripPlan;
import com.umc.yeogi_gal_lae.api.tripPlan.domain.TripType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripPlanRepository extends JpaRepository<TripPlan, Long> {
    List<TripPlan> findByUserId(Long userId);

    // 특정 사용자 ID와 여행 유형(TripType)에 따라 여행 목록 조회
    List<TripPlan> findByUserIdAndTripType(Long userId, TripType tripType);

    /**
     * 특정 사용자의 완료된 여행 계획을 조회합니다.
     * 완료된 여행: 종료일(endDate)이 현재 날짜 이전인 여행
     *
     * @param userId 사용자 ID
     * @return 완료된 여행 계획 목록
     */
    @Query("SELECT t FROM TripPlan t WHERE t.user.id = :userId AND t.endDate < CURRENT_DATE")
    List<TripPlan> findCompletedTripsByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM TripPlan t WHERE t.user.id = :userId AND t.startDate > CURRENT_DATE")
    List<TripPlan> findPlannedTripsByUserId(@Param("userId") Long userId);
}
