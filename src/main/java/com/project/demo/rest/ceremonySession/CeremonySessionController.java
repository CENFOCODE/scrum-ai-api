package com.project.demo.rest.ceremonySession;


import com.project.demo.logic.entity.ceremonySession.CeremonySession;
import com.project.demo.logic.entity.ceremonySession.CeremonySessionRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.simulation.Simulation;
import com.project.demo.logic.entity.simulation.SimulationRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ceremony-session")
public class CeremonySessionController {
    @Autowired
    private CeremonySessionRepository ceremonySessionRepository;

    @Autowired
    private SimulationRepository simulationRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createCeremonySession(@RequestBody Map<String, Object> request, HttpServletRequest req) {


        if (request.get("simulationId") == null) {
            return new GlobalResponseHandler().handleResponse(
                    "Simulation ID is required",
                    HttpStatus.BAD_REQUEST,
                    req
            );
        }

        Long simulationId = ((Number) request.get("simulationId")).longValue();


        Optional<Simulation> simulation = simulationRepository.findById(simulationId);

        if (simulation.isPresent()) {
            CeremonySession session = new CeremonySession();
            session.setCeremonyType((String) request.get("ceremonyType"));
            session.setSimulation(simulation.get());


            CeremonySession savedSession = ceremonySessionRepository.save(session);

            return new GlobalResponseHandler().handleResponse(
                    "CeremonySession created successfully",
                    savedSession,
                    HttpStatus.CREATED,
                    req
            );
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Simulation not found",
                    HttpStatus.BAD_REQUEST,
                    req
            );
        }
    }
}