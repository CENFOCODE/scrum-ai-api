package com.project.demo.rest.backlog;

import com.project.demo.logic.entity.backlog.BacklogItem;
import com.project.demo.logic.entity.backlog.BacklogItemRepository;
import com.project.demo.logic.entity.backlog.BacklogSprint;
import com.project.demo.logic.entity.backlog.BacklogSprintRepository;
import com.project.demo.logic.entity.backlog.BacklogSubtask;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.planningTicket.PlanningTicket;
import com.project.demo.logic.entity.planningTicket.PlanningTicketRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/backlog")
public class BacklogRestController {

    @Autowired
    private BacklogSprintRepository backlogSprintRepository;

    @Autowired
    private BacklogItemRepository backlogItemRepository;

    @Autowired
    private PlanningTicketRepository planningTicketRepository;

    @GetMapping
    public ResponseEntity<?> getBacklog(HttpServletRequest request) {
        syncPlanningTicketsToBacklog();

        List<BacklogSprint> sprints = backlogSprintRepository.findAll();
        sprints.sort(Comparator.comparing(BacklogSprint::getId));

        return new GlobalResponseHandler().handleResponse(
                "Backlog retrieved successfully",
                sprints,
                HttpStatus.OK,
                request
        );
    }

    private void syncPlanningTicketsToBacklog() {
        BacklogSprint backlogSprint = backlogSprintRepository.findByName("Backlog")
                .orElseGet(() -> {
                    BacklogSprint s = new BacklogSprint();
                    s.setName("Backlog");
                    s.setGoal("Historias disponibles para planificar.");
                    return backlogSprintRepository.save(s);
                });

        Set<Long> existingPlanningIds = new HashSet<>();
        for (BacklogItem item : backlogSprint.getItems()) {
            if (item.getPlanningTicketId() != null) {
                existingPlanningIds.add(item.getPlanningTicketId());
            }
        }

        List<PlanningTicket> planningTickets = planningTicketRepository.findAll();

        boolean modified = false;

        for (PlanningTicket pt : planningTickets) {
            if (pt.getId() == null) continue;
            if (existingPlanningIds.contains(pt.getId())) continue;

            BacklogItem item = new BacklogItem();
            item.setKey(
                    pt.getCode() != null && !pt.getCode().isBlank()
                            ? pt.getCode()
                            : "SCRUM-" + pt.getId()
            );
            item.setTitle(
                    pt.getName() != null && !pt.getName().isBlank()
                            ? pt.getName()
                            : "Historia sin título"
            );
            item.setModuleName(pt.getModule());
            item.setStatus("TO DO");
            item.setStoryPoints(pt.getStoryPoints());
            item.setDescription(pt.getDescription());
            item.setPlanningTicketId(pt.getId());

            backlogSprint.addItem(item);
            modified = true;
        }

        if (modified) {
            backlogSprintRepository.save(backlogSprint);
        }
    }

    // SPRINTS //

    @PostMapping("/sprints")
    public ResponseEntity<?> createSprint(HttpServletRequest request) {
        long count = backlogSprintRepository.count();

        BacklogSprint sprint = new BacklogSprint();
        sprint.setName("SC Sprint " + (count + 1));
        sprint.setGoal("");
        BacklogSprint saved = backlogSprintRepository.save(sprint);

        return new GlobalResponseHandler().handleResponse(
                "Sprint creado correctamente",
                saved,
                HttpStatus.CREATED,
                request
        );
    }

    @DeleteMapping("/sprints/{id}")
    public ResponseEntity<?> deleteSprint(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        backlogSprintRepository.deleteById(id);

        return new GlobalResponseHandler().handleResponse(
                "Sprint eliminado correctamente",
                null,
                HttpStatus.OK,
                request
        );
    }

    @PutMapping("/sprints/{id}")
    public ResponseEntity<?> updateSprint(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request
    ) {
        BacklogSprint sprint = backlogSprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        if (body.containsKey("name")) {
            String name = Optional.ofNullable((String) body.get("name")).orElse("").trim();
            if (!name.isEmpty()) {
                sprint.setName(name);
            }
        }

        if (body.containsKey("goal")) {
            sprint.setGoal((String) body.get("goal"));
        }

        if (body.containsKey("dates")) {
            sprint.setDates((String) body.get("dates"));
        }

        if (body.containsKey("startDate")) {
            sprint.setStartDate((String) body.get("startDate"));
        }

        if (body.containsKey("startTime")) {
            sprint.setStartTime((String) body.get("startTime"));
        }

        if (body.containsKey("endDate")) {
            sprint.setEndDate((String) body.get("endDate"));
        }

        if (body.containsKey("endTime")) {
            sprint.setEndTime((String) body.get("endTime"));
        }

        BacklogSprint saved = backlogSprintRepository.save(sprint);

        return new GlobalResponseHandler().handleResponse(
                "Sprint actualizado correctamente",
                saved,
                HttpStatus.OK,
                request
        );
    }

    // HISTORIAS //

    @PostMapping("/items")
    public ResponseEntity<?> createItem(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request
    ) {
        Object sprintIdRaw = body.get("sprintId");
        if (sprintIdRaw == null) {
            throw new RuntimeException("sprintId es requerido");
        }

        Long sprintId = Long.valueOf(sprintIdRaw.toString());

        BacklogSprint sprint = backlogSprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        BacklogItem item = new BacklogItem();
        item.setKey("SCRUM-0");
        item.setTitle("Nombre de historia del product backlog");
        item.setModuleName("Módulo");
        item.setStatus("TO DO");
        item.setStoryPoints(0);
        item.setDescription("");
        item.setPlanningTicketId(null);

        sprint.addItem(item);
        BacklogSprint saved = backlogSprintRepository.save(sprint);

        return new GlobalResponseHandler().handleResponse(
                "Historia creada correctamente",
                saved,
                HttpStatus.CREATED,
                request
        );
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteItem(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        BacklogItem item = backlogItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historia no encontrada"));

        BacklogSprint sprint = item.getSprint();
        sprint.removeItem(item);
        backlogSprintRepository.save(sprint);

        return new GlobalResponseHandler().handleResponse(
                "Historia eliminada correctamente",
                null,
                HttpStatus.OK,
                request
        );
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<?> updateItem(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request
    ) {
        BacklogItem item = backlogItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historia no encontrada"));

        if (body.containsKey("title")) {
            String title = Optional.ofNullable((String) body.get("title")).orElse("").trim();
            if (!title.isEmpty()) {
                item.setTitle(title);
            }
        }

        if (body.containsKey("key")) {
            String key = Optional.ofNullable((String) body.get("key"))
                    .orElse("")
                    .trim();
            if (!key.isEmpty()) {
                item.setKey(key);
            }
        }

        // módulo
        if (body.containsKey("module")) {
            String module = (String) body.get("module");
            if (module != null) {
                item.setModuleName(module.trim());
            } else {
                item.setModuleName(null);
            }
        }

        // estado
        if (body.containsKey("status")) {
            String status = (String) body.get("status");
            item.setStatus(status);
        }

        // story points
        if (body.containsKey("storyPoints")) {
            Object raw = body.get("storyPoints");
            if (raw != null) {
                int sp = Integer.parseInt(raw.toString());
                item.setStoryPoints(sp);
            } else {
                item.setStoryPoints(null);
            }
        }

        // descripción
        if (body.containsKey("description")) {
            String desc = (String) body.get("description");
            item.setDescription(desc);
        }

        // mover historia a otro sprint
        if (body.containsKey("sprintId")) {
            Object rawSprintId = body.get("sprintId");
            if (rawSprintId != null) {
                Long newSprintId = Long.valueOf(rawSprintId.toString());

                BacklogSprint currentSprint = item.getSprint();

                if (currentSprint != null && Objects.equals(currentSprint.getId(), newSprintId)) {
                } else {
                    if (currentSprint != null) {
                        currentSprint.removeItem(item);
                        backlogSprintRepository.save(currentSprint);
                    }

                    BacklogSprint newSprint = backlogSprintRepository.findById(newSprintId)
                            .orElseThrow(() -> new RuntimeException("Sprint destino no encontrado"));

                    if (!newSprint.getItems().contains(item)) {
                        newSprint.addItem(item);
                    }

                    backlogSprintRepository.save(newSprint);
                }
            }
        }

        if (body.containsKey("subtasks")) {
            List<BacklogSubtask> currentSubtasks = new ArrayList<>(item.getSubtasks());
            for (BacklogSubtask st : currentSubtasks) {
                st.setItem(null);
            }
            item.getSubtasks().clear();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> subtasks =
                    (List<Map<String, Object>>) body.get("subtasks");

            if (subtasks != null) {
                for (Map<String, Object> stMap : subtasks) {
                    BacklogSubtask st = new BacklogSubtask();
                    st.setCode((String) stMap.get("id"));
                    st.setTitle((String) stMap.get("title"));
                    st.setDescription((String) stMap.get("description"));
                    st.setStatus((String) stMap.get("status"));
                    st.setItem(item);
                    item.getSubtasks().add(st);
                }
            }
        }

        BacklogItem savedItem = backlogItemRepository.save(item);
        BacklogSprint sprint = savedItem.getSprint();

        return new GlobalResponseHandler().handleResponse(
                "Historia actualizada correctamente",
                sprint,
                HttpStatus.OK,
                request
        );
    }
}
