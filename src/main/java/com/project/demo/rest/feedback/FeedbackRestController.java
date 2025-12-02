package com.project.demo.rest.feedback;

import com.project.demo.logic.entity.feedback.Feedback;
import com.project.demo.logic.entity.feedback.FeedbackRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/feedback")
public class FeedbackRestController {

    private final FeedbackRepository feedbackRepository;

    public FeedbackRestController(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @GetMapping("/{simulationId}")
    public Feedback getFeedbackBySimulationId(@PathVariable Long simulationId) {
        return feedbackRepository.findBySimulationId(simulationId)
                .orElseThrow(() -> new RuntimeException("No feedback found for simulation " + simulationId));
    }

    @GetMapping
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

}
