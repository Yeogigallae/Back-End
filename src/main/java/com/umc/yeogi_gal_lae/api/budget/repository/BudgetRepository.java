package com.umc.yeogi_gal_lae.api.budget.repository;

import com.umc.yeogi_gal_lae.api.budget.domain.Budget;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByAiCourse_Id(Long aiCourseId);
}
