package com.project.demo.logic.daily;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DailySessionRepository extends JpaRepository<DailySession, Long> {
    Optional<DailySession> findBySimulationId(Long simulationId);
}
