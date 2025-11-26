package com.project.demo.rest.simulation;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.simulation.Simulation;
import com.project.demo.logic.entity.simulation.SimulationRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulation")
public class SimulationRestController {
    @Autowired

    private SimulationRepository simulationRepository;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Simulation> simulationsPage = simulationRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(simulationsPage.getTotalPages());
        meta.setTotalElements(simulationsPage.getTotalElements());
        meta.setPageNumber(simulationsPage.getNumber() + 1);
        meta.setPageSize(simulationsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Simulations retrieved successfully",
                simulationsPage.getContent(), HttpStatus.OK, meta);
    }

    @PostMapping
    public ResponseEntity<Simulation> createSimulation(@RequestBody Simulation simulation) {
        Simulation savedSimulation = simulationRepository.save(simulation);
        return ResponseEntity.ok(savedSimulation);
    }
}

