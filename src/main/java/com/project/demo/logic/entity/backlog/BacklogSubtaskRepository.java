package com.project.demo.logic.entity.backlog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BacklogSubtaskRepository extends JpaRepository<BacklogSubtask, Long> {
}
