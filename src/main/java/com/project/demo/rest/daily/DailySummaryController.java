package com.project.demo.rest.daily;

import com.project.demo.logic.daily.DailySummaryRequest;
import com.project.demo.logic.daily.DailySessionService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/daily")
public class DailySummaryController {

    private final DailySessionService dailyService;

    public DailySummaryController(DailySessionService dailyService) {
        this.dailyService = dailyService;
    }

    /**
     * Guarda el daily completo:
     * - Respuestas del usuario
     * - Estado del tablero
     * - Genera feedback con Groq
     * - Guarda feedback en la BD
     */
    @PostMapping("/save")
    public Map<String, Object> saveDaily(@RequestBody Map<String, Object> request) {
        return dailyService.saveDaily(request);
    }
}
