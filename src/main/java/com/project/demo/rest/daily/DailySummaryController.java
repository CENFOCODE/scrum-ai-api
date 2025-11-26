package com.project.demo.rest.daily;


import com.project.demo.logic.daily.DailySummaryRequest;
import com.project.demo.logic.daily.DailySummaryResponse;
import com.project.demo.logic.daily.DailySessionService;
import com.project.demo.logic.daily.SimulationDailyService;
import com.project.demo.logic.service.rtc.service.GroqService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/daily")
public class DailySummaryController {

    private final GroqService groqService;
    private final DailySessionService sessionService;
    private final SimulationDailyService metricDailyService;

    public DailySummaryController(GroqService groqService, DailySessionService sessionService, SimulationDailyService metricDailyService) {
        this.groqService = groqService;
        this.sessionService = sessionService;
        this.metricDailyService = metricDailyService;
    }

    @PostMapping("/summary")
    public DailySummaryResponse getAISummary(@RequestBody DailySummaryRequest request) {

        String aiAnswer = groqService.askDailySummary(request);

        DailySummaryResponse response = new DailySummaryResponse();
        response.setSummary(aiAnswer);
        response.setRecommendations("Generadas por IA...");
        response.setPriority("media");
        response.setRiskLevel("bajo");

        return response;
    }

    @PostMapping("/save")
    public String saveDaily(@RequestBody DailySummaryRequest request) {

        Long userId = 1L;
        Long simulationId = request.getSimulationId();

        // IA summary viene del front
        String aiSummary = request.getAiSummary();

        sessionService.saveDaily(request, aiSummary, userId);

        metricDailyService.saveDailyMetric(simulationId, userId, request);

        return "Daily guardada correctamente";
    }
}