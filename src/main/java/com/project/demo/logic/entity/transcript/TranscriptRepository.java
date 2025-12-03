package com.project.demo.logic.entity.transcript;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface TranscriptRepository extends JpaRepository<Transcript, Long> {

    List<Transcript> findByCeremonySessionIdOrderByTimestampAsc(Long ceremonySessionId);

}