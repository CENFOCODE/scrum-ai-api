package com.project.demo.rest.planning;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.planning.Planning;
import com.project.demo.logic.planning.PlanningRepository;
import com.project.demo.logic.planning.PlanningService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/planning")
public class PlanningRestController {

    private final PlanningService service;
    private final PlanningRepository planningRepository;

    public PlanningRestController(PlanningService service, PlanningRepository planningRepository) {
        this.service = service;
        this.planningRepository = planningRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Planning> simulationsPage = planningRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(simulationsPage.getTotalPages());
        meta.setTotalElements(simulationsPage.getTotalElements());
        meta.setPageNumber(simulationsPage.getNumber() + 1);
        meta.setPageSize(simulationsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Planning data retrieved successfully",
                simulationsPage.getContent(), HttpStatus.OK, meta);
    }

    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody Map<String, Object> request) {

        Map<String, Object> result = service.save(request);

        Planning plan = (Planning) result.get("planning");
        String feedbackMessage = (String) result.get("feedbackMessage");

        Map<String, Object> response = new HashMap<>();
        response.put("planningId", plan.getId());
        response.put("feedbackMessage", feedbackMessage);
        response.put("message", "Planning generado y feedback almacenado.");

        return response;
    }

    @GetMapping("/{simulationId}")
    public Planning get(@PathVariable Long simulationId) {
        return service.getBySimulation(simulationId);
    }
}
