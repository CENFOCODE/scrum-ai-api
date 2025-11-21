package com.project.demo.logic.daily;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DailySessionRepository extends JpaRepository<DailySession, Long> {
}
