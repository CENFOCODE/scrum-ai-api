package com.project.demo.logic.entity.scenarioTemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScenarioTemplateRepository extends JpaRepository<ScenarioTemplate, Long> {

    @Query("SELECT st FROM ScenarioTemplate st WHERE st.scenario.id = :scenarioId AND st.stepOrder = :stepOrder")

    List<ScenarioTemplate> findByScenarioIdOrderByStepOrder(Long scenarioId);

    Optional<ScenarioTemplate> findByScenarioIdAndStepOrder(Long scenarioId, Integer stepOrder);


}
