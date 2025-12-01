package com.project.demo.logic.retrospective;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RetrospectiveRepository extends JpaRepository<Retrospective, Long> {

    Optional<Retrospective> findBySimulationId(Long simulationId);
}
