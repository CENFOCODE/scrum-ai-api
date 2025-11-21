package com.project.demo.logic.entity.auth.daily;

import com.project.demo.logic.dtos.daily.DailySummaryRequest;
import com.project.demo.logic.daily.DailySession;
import com.project.demo.logic.daily.DailySessionRepository;
import com.project.demo.utils.JsonUtils;
import org.springframework.stereotype.Service;

@Service
public class DailySessionService {

    private final DailySessionRepository repository;

    public DailySessionService(DailySessionRepository repository) {
        this.repository = repository;
    }

    public DailySession saveDaily(DailySummaryRequest req, String aiSummary, Long userId) {

        DailySession session = new DailySession();

        session.setUserId(userId);

        // Convertimos tus objetos Java a JSON
        session.setAnswersJson(JsonUtils.toJson(req.getAnswers()));
        session.setBoardJson(JsonUtils.toJson(req.getBoard()));

        session.setAiSummary(aiSummary);

        return repository.save(session);
    }
}
