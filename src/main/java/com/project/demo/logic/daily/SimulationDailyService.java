package com.project.demo.logic.daily;

import com.project.demo.logic.entity.simulation.Simulation;
import com.project.demo.logic.entity.simulation.SimulationRepository;
import com.project.demo.logic.entity.simulationMetric.SimulationMetric;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.simulationMetric.SimulationMetricRepository;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.utils.JsonUtils;
import org.springframework.stereotype.Service;

@Service
public class SimulationDailyService {

    private final SimulationMetricRepository metricRepo;
    private final SimulationRepository simulationRepo;
    private final UserRepository userRepo;

    public SimulationDailyService(
            SimulationMetricRepository metricRepo,
            SimulationRepository simulationRepo,
            UserRepository userRepo
    ) {
        this.metricRepo = metricRepo;
        this.simulationRepo = simulationRepo;
        this.userRepo = userRepo;
    }

    public SimulationMetric saveDailyMetric(Long simulationId, Long userId, DailySummaryRequest request) {

        Simulation simulation = simulationRepo.findById(simulationId)
                .orElseThrow(() -> new RuntimeException("Simulation not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Convertimos TODO el DailySummaryRequest a JSON
        String json = JsonUtils.toJson(request);

        SimulationMetric metric = new SimulationMetric();
        metric.setSimulation(simulation);
        metric.setUser(user);
        metric.setMetricName("daily");
        metric.setMetricCategory("ceremony");
        metric.setMetricValue(1.0);
        metric.setMetricData(json);

        return metricRepo.save(metric);
    }
}
