package com.project.demo.rest.role;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/scrum-roles")
public class ScrumRoleRestController {

    @GetMapping("/scrum-data")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getScrumData(HttpServletRequest request) {

        // Datos quemados
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("ceremony", "Daily Scrum");
        data.put("intro", "El Daily Scrum es una reunión diaria de corta duración en la que el equipo sincroniza esfuerzos, identifica bloqueos y planifica el trabajo del día para mantener el avance hacia el Sprint Goal.");
        data.put("objective", "Asegurar la sincronización del equipo en torno al Sprint Goal mediante: la visibilidad diaria del progreso y de las dependencias entre tareas, la priorización de las actividades de las próximas 24, la identificación temprana de impedimentos y la actualización ligera del Sprint Backlog cuando sea necesario, manteniendo la coordinación entre miembros para equilibrar carga de trabajo, compartir contexto y reducir riesgos de integración.");
        data.put("scenario", "El equipo de desarrollo está a mitad del Sprint. Algunos miembros han completado historias del backlog, mientras otros enfrentan bloqueos técnicos con la integración del módulo de pagos. El Scrum Master facilita la reunión para que cada integrante responda:\n" +
                "¿Qué hice ayer? ¿Qué haré hoy? ¿Qué impedimentos tengo?");
        data.put("participants", Arrays.asList("Max Verstappen Scrum Master", "Fernando Alonso Product Owner", "Charles Leclerc Developer", "Oscar Piastri Stakeholder"));

        List<Map<String, Object>> roles = new ArrayList<>();
        roles.add(Map.of("id", 1, "name", "Scrum Master"));
        roles.add(Map.of("id", 2, "name", "Product Owner"));
        roles.add(Map.of("id", 3, "name", "Developer"));
        roles.add(Map.of("id", 4, "name", "Stakeholder"));
        data.put("roles", roles);

        return new GlobalResponseHandler().handleResponse(
                "Scrum data retrieved successfully",
                data,
                HttpStatus.OK,
                new Meta(request.getMethod(), request.getRequestURL().toString())
        );
    }

    @PostMapping("/assign")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> assignRole(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Object userId = body.get("userId");
        Object roleId = body.get("roleId");
        if (userId == null || roleId == null) {
            return new GlobalResponseHandler().handleResponse(
                    "userId and roleId are required",
                    HttpStatus.BAD_REQUEST,
                    request
            );
        }
        String msg = String.format("Role %s assigned to user %s", roleId, userId);
        return new GlobalResponseHandler().handleResponse(msg, null, HttpStatus.OK, request);
    }
}
