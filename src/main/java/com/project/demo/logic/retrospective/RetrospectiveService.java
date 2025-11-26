package com.project.demo.logic.retrospective;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RetrospectiveService {

    private final RetrospectiveRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    public RetrospectiveService(RetrospectiveRepository repo) {
        this.repo = repo;
    }

    public Retrospective save(Map<String, Object> request) {

        Long simulationId = Long.valueOf(request.get("simulationId").toString());

        Map<String, Object> retrospectiveData =
                (Map<String, Object>) request.get("retrospective");

        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(retrospectiveData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Retrospective r = repo.findBySimulationId(simulationId)
                .orElse(new Retrospective());

        r.setSimulationId(simulationId);
        r.setRetrospectiveJson(jsonString);

        return repo.save(r);
    }

    public Retrospective getBySimulation(Long simulationId) {
        return repo.findBySimulationId(simulationId).orElse(null);
    }
}
