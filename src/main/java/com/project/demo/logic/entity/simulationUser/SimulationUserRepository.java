package com.project.demo.logic.entity.simulationUser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SimulationUserRepository extends JpaRepository<SimulationUser, Long> {
    Optional<SimulationUser> findBySimulationIdAndUserId(Long simulationId, Long userId);

}
