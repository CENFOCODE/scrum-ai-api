package com.project.demo.rest.retrospective;

import com.project.demo.logic.retrospective.Retrospective;
import com.project.demo.logic.retrospective.RetrospectiveService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/retrospective")
public class RetrospectiveController {

    private final RetrospectiveService service;

    public RetrospectiveController(RetrospectiveService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public Retrospective save(@RequestBody Map<String, Object> request) {
        return service.save(request);
    }

    @GetMapping("/{simulationId}")
    public Retrospective get(@PathVariable Long simulationId) {
        return service.getBySimulation(simulationId);
    }
}
