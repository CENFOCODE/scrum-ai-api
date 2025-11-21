package com.project.demo.rest.daily;


import com.project.demo.logic.dtos.daily.DailySummaryRequest;
import com.project.demo.logic.dtos.daily.DailySummaryResponse;
import com.project.demo.logic.entity.auth.daily.DailySessionService;
import com.project.demo.logic.service.rtc.service.GroqService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/daily")
@CrossOrigin("*")
public class DailySummaryController {

    private final GroqService groqService;
    private final DailySessionService sessionService;

    public DailySummaryController(GroqService groqService, DailySessionService sessionService) {
        this.groqService = groqService;
        this.sessionService = sessionService;
    }

    @PostMapping("/summary")
    public DailySummaryResponse getAISummary(@RequestBody DailySummaryRequest request) {

        // Usamos el servicio de IA ya existente
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

        // TODO: obtener userId del JWT
        Long userId = 1L;

        // IA summary viene del front
        String aiSummary = request.getAnswers().getToday();

        sessionService.saveDaily(request, aiSummary, userId);

        return "Daily guardada correctamente";
    }
}