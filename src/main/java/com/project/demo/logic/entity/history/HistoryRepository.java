package com.project.demo.logic.entity.history;

import com.project.demo.logic.entity.history.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> findByUserId(Long userId);

    @Query("SELECT h FROM History h WHERE h.user.id = :userId AND LOWER(h.simulation.scenario.ceremonyType) = LOWER(:ceremonyType)")
    List<History> findByUserAndCeremony(@Param("userId") Long userId, @Param("ceremonyType") String ceremonyType);

    boolean existsBySimulationId(Long simulationId);

}
