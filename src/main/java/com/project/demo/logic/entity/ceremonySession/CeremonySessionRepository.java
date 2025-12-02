package com.project.demo.logic.entity.ceremonySession;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CeremonySessionRepository extends JpaRepository<CeremonySession, Long> {
    Optional<CeremonySession> findByCeremonyType(String ceremonyType);
}
