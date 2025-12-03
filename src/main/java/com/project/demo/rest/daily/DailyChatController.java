package com.project.demo.rest.daily;

import com.project.demo.logic.daily.DailyChatRequest;
import com.project.demo.logic.service.rtc.service.GroqService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/daily")
@CrossOrigin("*")
public class DailyChatController {

    private final GroqService groqService;

    public DailyChatController(GroqService groqService) {
        this.groqService = groqService;
    }

    @PostMapping("/chat")
    public String chatDaily(@RequestBody DailyChatRequest request) {
        return groqService.askDailyChat(request);
    }
}
