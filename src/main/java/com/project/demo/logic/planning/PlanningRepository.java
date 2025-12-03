package com.project.demo.logic.planning;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanningRepository extends JpaRepository<Planning, Long> {

    Optional<Planning> findBySimulationId(Long simulationId);
}
