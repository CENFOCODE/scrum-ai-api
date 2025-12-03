package com.project.demo.rest.planningTicket;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.planningTicket.PlanningTicket;
import com.project.demo.logic.entity.planningTicket.PlanningTicketRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planning-ticket")
public class PlanningTicketRestController {

    @Autowired
    private PlanningTicketRepository planningTicketRepository;

    @GetMapping
    public ResponseEntity<?> getAll(HttpServletRequest request) {
        List<PlanningTicket> tickets = planningTicketRepository.findAll();

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalElements((long) tickets.size());
        meta.setTotalPages(1);
        meta.setPageNumber(1);
        meta.setPageSize(tickets.size());

        return new GlobalResponseHandler().handleResponse(
                "Planning tickets retrieved successfully",
                tickets,
                HttpStatus.OK,
                meta
        );
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PlanningTicket ticket, HttpServletRequest request) {
        if (ticket.getStatus() == null || ticket.getStatus().isBlank()) {
            ticket.setStatus("Pendiente");
        }
        PlanningTicket saved = planningTicketRepository.save(ticket);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        return new GlobalResponseHandler().handleResponse(
                "Planning ticket created successfully",
                saved,
                HttpStatus.CREATED,
                meta
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody PlanningTicket requestTicket,
            HttpServletRequest request
    ) {
        PlanningTicket existing = planningTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planning ticket not found"));

        existing.setCode(requestTicket.getCode());
        existing.setName(requestTicket.getName());
        existing.setDescription(requestTicket.getDescription());
        existing.setModule(requestTicket.getModule());
        existing.setStoryPoints(requestTicket.getStoryPoints());
        existing.setStatus(requestTicket.getStatus());

        PlanningTicket saved = planningTicketRepository.save(existing);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        return new GlobalResponseHandler().handleResponse(
                "Planning ticket updated successfully",
                saved,
                HttpStatus.OK,
                meta
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        planningTicketRepository.deleteById(id);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        return new GlobalResponseHandler().handleResponse(
                "Planning ticket deleted successfully",
                null,
                HttpStatus.OK,
                meta
        );
    }
}
