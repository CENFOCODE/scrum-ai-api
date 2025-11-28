package com.project.demo.logic.entity.history;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {

    private final HistoryRepository repo;

    public HistoryService(HistoryRepository repo) {
        this.repo = repo;
    }

    public List<History> getHistoryByUser(Long userId) {
        return repo.findByUserId(userId);
    }

    public List<History> getFiltered(Long userId, String scenario) {
        return repo.findByUserAndCeremony(userId, scenario);
    }
}

