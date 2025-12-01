package com.project.demo.logic.entity.scenarioTemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScenarioTemplateRepository extends JpaRepository<ScenarioTemplate, Long> {

    @Query("SELECT st FROM ScenarioTemplate st WHERE st.scenario.id = :scenarioId AND st.stepOrder = :stepOrder")
    ScenarioTemplate findByScenarioIdAndStepOrder(@Param("scenarioId") Long scenarioId, @Param("stepOrder") Integer stepOrder);
}
