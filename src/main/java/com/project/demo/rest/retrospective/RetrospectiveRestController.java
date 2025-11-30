package com.project.demo.rest.retrospective;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.simulation.Simulation;
import com.project.demo.logic.retrospective.Retrospective;
import com.project.demo.logic.retrospective.RetrospectiveRepository;
import com.project.demo.logic.retrospective.RetrospectiveService;
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
@RequestMapping("/retrospective")
public class RetrospectiveRestController {

    private final RetrospectiveService service;
    private final RetrospectiveRepository retrospectiveRepository;


    public RetrospectiveRestController(RetrospectiveService service, RetrospectiveRepository retrospectiveRepository) {
        this.service = service;
        this.retrospectiveRepository = retrospectiveRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Retrospective> simulationsPage = retrospectiveRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(simulationsPage.getTotalPages());
        meta.setTotalElements(simulationsPage.getTotalElements());
        meta.setPageNumber(simulationsPage.getNumber() + 1);
        meta.setPageSize(simulationsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Retrospective data retrieved successfully",
                simulationsPage.getContent(), HttpStatus.OK, meta);
    }

    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody Map<String, Object> request) {

        Retrospective retro = service.save(request);

        Map<String, Object> response = new HashMap<>();
        response.put("retrospectiveId", retro.getId());
        response.put("message", "Retrospective generada y feedback almacenado.");

        return response;
    }


    @GetMapping("/{simulationId}")
    public Retrospective get(@PathVariable Long simulationId) {
        return service.getBySimulation(simulationId);
    }
}
