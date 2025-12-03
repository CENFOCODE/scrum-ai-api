package com.project.demo.logic.entity.planningTicket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanningTicketRepository extends JpaRepository<PlanningTicket, Long> {
}
