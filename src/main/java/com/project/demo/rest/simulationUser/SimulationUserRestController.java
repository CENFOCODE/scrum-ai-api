package com.project.demo.rest.simulationUser;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.simulation.Simulation;
import com.project.demo.logic.entity.simulation.SimulationRepository;
import com.project.demo.logic.entity.simulationUser.SimulationUser;
import com.project.demo.logic.entity.simulationUser.SimulationUserRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulationUser")
public class SimulationUserRestController {

    @Autowired
    private SimulationUserRepository simulationUserRepository;

    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<SimulationUser> simulationUsersPage = simulationUserRepository.findAll(pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(simulationUsersPage.getTotalPages());
        meta.setTotalElements(simulationUsersPage.getTotalElements());
        meta.setPageNumber(simulationUsersPage.getNumber() + 1);
        meta.setPageSize(simulationUsersPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Simulation user retrieved successfully",
                simulationUsersPage.getContent(),
                HttpStatus.OK,
                meta
        );
    }

    @PostMapping
    public ResponseEntity<SimulationUser> createSimulationUser(@RequestBody SimulationUser simulationUser) {

        // 1️⃣ VALIDAR Y CARGAR SIMULATION COMPLETA
        if (simulationUser.getSimulation() != null && simulationUser.getSimulation().getId() != null) {
            Simulation fullSim = simulationRepository.findById(simulationUser.getSimulation().getId())
                    .orElseThrow(() -> new RuntimeException("Simulation no encontrada"));

            simulationUser.setSimulation(fullSim);
        } else {
            throw new RuntimeException("Debe incluir el ID de la Simulation existente.");
        }

        // 2️⃣ VALIDAR Y CARGAR USER COMPLETO
        if (simulationUser.getUser() != null && simulationUser.getUser().getId() != null) {
            User fullUser = userRepository.findById(simulationUser.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User no encontrado"));

            simulationUser.setUser(fullUser);
        } else {
            throw new RuntimeException("Debe incluir el ID del User existente.");
        }

        // 3️⃣ GUARDAR
        SimulationUser saved = simulationUserRepository.save(simulationUser);

        return ResponseEntity.ok(saved);
    }
}
