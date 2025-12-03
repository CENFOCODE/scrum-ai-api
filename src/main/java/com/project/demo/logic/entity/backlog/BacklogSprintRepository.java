package com.project.demo.logic.entity.backlog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BacklogSprintRepository extends JpaRepository<BacklogSprint, Long> {

    Optional<BacklogSprint> findByName(String name);
}
