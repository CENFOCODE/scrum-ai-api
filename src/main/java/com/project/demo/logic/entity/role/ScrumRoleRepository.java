package com.project.demo.logic.entity.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrumRoleRepository extends JpaRepository<ScrumRole, Long> {
}
