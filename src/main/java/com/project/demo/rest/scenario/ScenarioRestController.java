package com.project.demo.rest.scenario;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.scenario.Scenario;
import com.project.demo.logic.entity.scenario.ScenarioRepository;
import com.project.demo.logic.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scenario")
public class ScenarioRestController {
@Autowired
private ScenarioRepository scenarioRepository;
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Scenario> scenariosPage = scenarioRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(scenariosPage.getTotalPages());
        meta.setTotalElements(scenariosPage.getTotalElements());
        meta.setPageNumber(scenariosPage.getNumber() + 1);
        meta.setPageSize(scenariosPage.getSize());

        return new GlobalResponseHandler().handleResponse("Scenario retrieved successfully",
                scenariosPage.getContent(), HttpStatus.OK, meta);
    }
}
