package com.project.demo.rest.history;

import com.project.demo.logic.entity.history.History;
import com.project.demo.logic.entity.history.HistoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/history")
public class HistoryRestController {

    private final HistoryRepository historyRepository;

    public HistoryRestController(HistoryRepository repo) {
        this.historyRepository = repo;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getHistory(@PathVariable String userId) {

        if (userId.equalsIgnoreCase("all")) {
            return ResponseEntity.ok(historyRepository.findAll());
        }

        try {
            Long id = Long.parseLong(userId);
            return ResponseEntity.ok(historyRepository.findByUserId(id));
        }
        catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid userId value: " + userId);
        }
    }


    @GetMapping("/{userId}/filter")
    public ResponseEntity<?> getFiltered(
            @PathVariable String userId,
            @RequestParam String ceremonyType
    ) {

        if (userId.equalsIgnoreCase("all")) {

            return ResponseEntity.ok(
                    historyRepository.findAll()
                            .stream()
                            .filter(h -> h.getSimulation()
                                    .getScenario()
                                    .getCeremonyType()
                                    .equalsIgnoreCase(ceremonyType))
                            .toList()
            );
        }

        try {
            Long id = Long.parseLong(userId);
            return ResponseEntity.ok(
                    historyRepository.findByUserAndCeremony(id, ceremonyType)
            );
        }
        catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid userId: " + userId);
        }
    }
}
