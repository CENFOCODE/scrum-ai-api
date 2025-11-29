package com.project.demo.rest.simulation;


import com.project.demo.logic.entity.history.History;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.simulation.Simulation;
import com.project.demo.logic.entity.simulation.SimulationRepository;
import com.project.demo.logic.entity.history.HistoryRepository;
import com.project.demo.logic.entity.simulationUser.SimulationUser;
import com.project.demo.logic.entity.simulationUser.SimulationUserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/simulation")
public class SimulationRestController {

    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private SimulationRepository simulationRepository;
    @Autowired
    private SimulationUserRepository simulationUserRepository;

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

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeSimulation(@PathVariable Long id) {

        Simulation simulation = simulationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Simulation not found"));

        // Cambiar estado
        simulation.setStatus("COMPLETED");
        simulation.setEndDate(new Date());

        // Guardar simulation actualizada
        simulation = simulationRepository.save(simulation);

        // 1️⃣ Obtener el usuario creador de la simulación
        Long userId = simulation.getCreatedBy().getId();

        // 2️⃣ Buscar SimulationUser
        Optional<SimulationUser> simUserOpt =
                simulationUserRepository.findBySimulationIdAndUserId(simulation.getId(), userId);

        SimulationUser simUser = simUserOpt.orElse(null);

        // 3️⃣ Crear el History
        History history = new History();
        history.setSimulation(simulation);
        history.setUser(simulation.getCreatedBy());
        history.setFinalScore(simulation.getAverageScore());
        history.setTranscript("Simulación finalizada correctamente.");

        // Solo si existe relación SimulationUser
        if (simUser != null) {
            history.setSimulationUser(simUser);
        }

        historyRepository.save(history);

        return ResponseEntity.ok(
                Map.of("message", "Simulation completed and history created.")
        );
    }
}

