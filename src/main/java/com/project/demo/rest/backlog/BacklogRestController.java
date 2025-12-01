package com.project.demo.rest.backlog;

import com.project.demo.logic.entity.backlog.*;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.planningTicket.PlanningTicket;
import com.project.demo.logic.entity.planningTicket.PlanningTicketRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
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
    private BacklogSubtaskRepository backlogSubtaskRepository;

    @Autowired
    private PlanningTicketRepository planningTicketRepository;

    @GetMapping
    public ResponseEntity<?> getBacklog(HttpServletRequest request) {
        syncPlanningTicketsToBacklog();

        List<BacklogSprint> all = backlogSprintRepository.findAll();

        BacklogSprint backlog = null;
        BacklogSprint completedContainer = null;
        List<BacklogSprint> others = new ArrayList<>();

        for (BacklogSprint sp : all) {
            if ("BACKLOG".equalsIgnoreCase(sp.getStatus())) backlog = sp;
            else if ("COMPLETED_CONTAINER".equalsIgnoreCase(sp.getStatus())) completedContainer = sp;
            else others.add(sp);
        }

        others.sort(Comparator.comparing(BacklogSprint::getId));

        List<BacklogSprint> ordered = new ArrayList<>();
        if (completedContainer != null) ordered.add(completedContainer);
        if (backlog != null) ordered.add(backlog);
        ordered.addAll(others);

        return new GlobalResponseHandler().handleResponse(
                "Backlog retrieved successfully", ordered, HttpStatus.OK, request
        );
    }

    // Sync planning //
    private void syncPlanningTicketsToBacklog() {

        BacklogSprint backlog = backlogSprintRepository.findByName("Backlog")
                .orElseGet(() -> {
                    BacklogSprint s = new BacklogSprint();
                    s.setName("Backlog");
                    s.setGoal("Historias disponibles para planificar.");
                    s.setStatus("BACKLOG");
                    return backlogSprintRepository.save(s);
                });

        backlogSprintRepository.findByName("Sprints completados")
                .orElseGet(() -> {
                    BacklogSprint s = new BacklogSprint();
                    s.setName("Sprints completados");
                    s.setGoal("Historial de sprints completados.");
                    s.setStatus("COMPLETED_CONTAINER");
                    return backlogSprintRepository.save(s);
                });

        Set<Long> existing = new HashSet<>();
        for (BacklogItem it : backlog.getItems()) {
            if (it.getPlanningTicketId() != null) existing.add(it.getPlanningTicketId());
        }

        boolean modified = false;

        for (PlanningTicket pt : planningTicketRepository.findAll()) {
            if (pt.getId() == null) continue;
            if (existing.contains(pt.getId())) continue;

            BacklogItem it = new BacklogItem();
            it.setKey(pt.getCode() != null ? pt.getCode() : ("SCRUM-" + pt.getId()));
            it.setTitle(pt.getName() != null ? pt.getName() : "Historia sin título");
            it.setModuleName(pt.getModule());
            it.setStatus("TO DO");
            it.setStoryPoints(pt.getStoryPoints());
            it.setDescription(pt.getDescription());
            it.setPlanningTicketId(pt.getId());

            backlog.addItem(it);
            modified = true;
        }

        if (modified) backlogSprintRepository.save(backlog);
    }

    // SPRINTS //
    @PostMapping("/sprints")
    public ResponseEntity<?> createSprint(HttpServletRequest request) {

        long count = backlogSprintRepository.count();

        BacklogSprint sprint = new BacklogSprint();
        sprint.setName("SC Sprint " + (count + 1));
        sprint.setGoal("");
        sprint.setStatus("PENDING");

        BacklogSprint saved = backlogSprintRepository.save(sprint);

        return new GlobalResponseHandler().handleResponse(
                "Sprint creado correctamente", saved, HttpStatus.CREATED, request
        );
    }

    // START SPRINT //
    @PutMapping("/sprints/{id}/start")
    public ResponseEntity<?> startSprint(@PathVariable Long id, HttpServletRequest request) {

        BacklogSprint sprint = backlogSprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        if ("BACKLOG".equalsIgnoreCase(sprint.getStatus())
                || "COMPLETED_CONTAINER".equalsIgnoreCase(sprint.getStatus())) {

            return new GlobalResponseHandler().handleResponse(
                    "No se puede iniciar este sprint", null, HttpStatus.BAD_REQUEST, request
            );
        }

        sprint.setStatus("ACTIVE");
        backlogSprintRepository.save(sprint);

        return new GlobalResponseHandler().handleResponse(
                "Sprint iniciado correctamente", sprint, HttpStatus.OK, request
        );
    }

    // COMPLETE SPRINT //
    @PutMapping("/sprints/{id}/complete")
    public ResponseEntity<?> completeSprint(@PathVariable Long id, HttpServletRequest request) {

        BacklogSprint sprint = backlogSprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        if ("BACKLOG".equalsIgnoreCase(sprint.getStatus())
                || "COMPLETED_CONTAINER".equalsIgnoreCase(sprint.getStatus())) {

            return new GlobalResponseHandler().handleResponse(
                    "No se puede completar este sprint", null, HttpStatus.BAD_REQUEST, request
            );
        }

        sprint.setStatus("COMPLETED");

        BacklogSprint container = backlogSprintRepository.findByName("Sprints completados")
                .orElseThrow(() -> new RuntimeException("Contenedor no encontrado"));

        sprint.setParent(container);

        backlogSprintRepository.save(sprint);

        return new GlobalResponseHandler().handleResponse(
                "Sprint completado correctamente", sprint, HttpStatus.OK, request
        );
    }

    // DELETE SPRINT //
    @DeleteMapping("/sprints/{id}")
    public ResponseEntity<?> deleteSprint(@PathVariable Long id, HttpServletRequest request) {

        backlogSprintRepository.deleteById(id);

        return new GlobalResponseHandler().handleResponse(
                "Sprint eliminado correctamente", null, HttpStatus.OK, request
        );
    }

    // UPDATE SPRINT //
    @PutMapping("/sprints/{id}")
    public ResponseEntity<?> updateSprint(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request
    ) {
        BacklogSprint sprint = backlogSprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        if (body.containsKey("name")) sprint.setName((String) body.get("name"));
        if (body.containsKey("goal")) sprint.setGoal((String) body.get("goal"));
        if (body.containsKey("dates")) sprint.setDates((String) body.get("dates"));
        if (body.containsKey("startDate")) sprint.setStartDate((String) body.get("startDate"));
        if (body.containsKey("startTime")) sprint.setStartTime((String) body.get("startTime"));
        if (body.containsKey("endDate")) sprint.setEndDate((String) body.get("endDate"));
        if (body.containsKey("endTime")) sprint.setEndTime((String) body.get("endTime"));

        backlogSprintRepository.save(sprint);

        return new GlobalResponseHandler().handleResponse(
                "Sprint actualizado correctamente", sprint, HttpStatus.OK, request
        );
    }

    // CREAR STORY //
    @PostMapping("/items")
    public ResponseEntity<?> createItem(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request
    ) {
        Long sprintId = Long.valueOf(body.get("sprintId").toString());

        BacklogSprint sprint = backlogSprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        BacklogItem item = new BacklogItem();
        item.setKey("SCRUM-0");
        item.setTitle("Nombre de historia del product backlog");
        item.setModuleName("Módulo");
        item.setStatus("TO DO");
        item.setStoryPoints(0);
        item.setDescription("");

        sprint.addItem(item);

        backlogSprintRepository.save(sprint);

        return new GlobalResponseHandler().handleResponse(
                "Historia creada correctamente", sprint, HttpStatus.CREATED, request
        );
    }

    // DELETE STORY //
    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id, HttpServletRequest request) {

        BacklogItem item = backlogItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historia no encontrada"));

        BacklogSprint sprint = item.getSprint();
        sprint.removeItem(item);

        backlogSprintRepository.save(sprint);

        return new GlobalResponseHandler().handleResponse(
                "Historia eliminada correctamente", sprint, HttpStatus.OK, request
        );
    }

    // UPDATE ITEM + SUBTASKS //
    @Transactional
    @PutMapping("/items/{id}")
    public ResponseEntity<?> updateItem(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request
    ) {

        BacklogItem item = backlogItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historia no encontrada"));

        if (body.containsKey("title")) item.setTitle((String) body.get("title"));
        if (body.containsKey("key")) item.setKey((String) body.get("key"));
        if (body.containsKey("module")) item.setModuleName((String) body.get("module"));
        if (body.containsKey("status")) item.setStatus((String) body.get("status"));
        if (body.containsKey("storyPoints"))
            item.setStoryPoints(Integer.parseInt(body.get("storyPoints").toString()));
        if (body.containsKey("description"))
            item.setDescription((String) body.get("description"));


        // MOVER HISTORIA //
        if (body.containsKey("sprintId")) {

            Long newSprintId = Long.valueOf(body.get("sprintId").toString());
            BacklogSprint oldSprint = item.getSprint();

            if (!Objects.equals(oldSprint.getId(), newSprintId)) {

                BacklogSprint newSprint = backlogSprintRepository.findById(newSprintId)
                        .orElseThrow(() -> new RuntimeException("Sprint destino no encontrado"));

                item.setSprint(newSprint);
            }
        }

        // SUBTAREAS //
        if (body.containsKey("subtasks")) {

            List<Map<String, Object>> subtasksBody =
                    (List<Map<String, Object>>) body.get("subtasks");

            // obtener la lista manejada por Hibernate
            List<BacklogSubtask> existing = item.getSubtasks();
            List<BacklogSubtask> keep = new ArrayList<>();

            for (Map<String, Object> stBody : subtasksBody) {

                Long stId = null;
                try {
                    if (stBody.get("id") != null) {
                        String raw = String.valueOf(stBody.get("id"));
                        if (!raw.trim().isEmpty()) stId = Long.valueOf(raw);
                    }
                } catch (Exception ignored) {
                }

                BacklogSubtask st = null;

                // Buscar subtarea existente
                if (stId != null) {
                    for (BacklogSubtask e : existing) {
                        if (Objects.equals(e.getId(), stId)) {
                            st = e;
                            break;
                        }
                    }
                }

                // Crear nueva subtarea si no existía
                if (st == null) {
                    st = new BacklogSubtask();
                    st.setItem(item);
                    existing.add(st);
                }

                // Actualizar datos
                st.setCode((String) stBody.getOrDefault("code", null));
                st.setTitle((String) stBody.getOrDefault("title", ""));
                st.setDescription((String) stBody.getOrDefault("description", ""));
                st.setStatus((String) stBody.getOrDefault("status", "TO DO"));

                keep.add(st);
            }

            // Eliminar las que ya no están
            existing.removeIf(st -> !keep.contains(st));
        }


        // Guardar //
        backlogItemRepository.save(item);

        BacklogSprint responseSprint = backlogSprintRepository.findById(item.getSprint().getId())
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        // carga de subtareas (lazy loading fix)
        responseSprint.getItems().forEach(it -> it.getSubtasks().size());


        return new GlobalResponseHandler().handleResponse(
                "Historia actualizada correctamente",
                item,
                HttpStatus.OK,
                request
        );
    }
}
