package com.project.demo.logic.entity.backlog;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "backlog_item")
public class BacklogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "story_key", nullable = false, length = 50)
    private String key;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "module_name", length = 100)
    private String moduleName;

    @Column(length = 20)
    private String status;

    @Column(name = "story_points")
    private Integer storyPoints;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "planning_ticket_id")
    private Long planningTicketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    @JsonBackReference
    private BacklogSprint sprint;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BacklogSubtask> subtasks = new ArrayList<>();

    @JsonProperty("sprintId")
    public Long getSprintId() {
        return sprint != null ? sprint.getId() : null;
    }

    public BacklogItem() {
    }

    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPlanningTicketId() {
        return planningTicketId;
    }

    public void setPlanningTicketId(Long planningTicketId) {
        this.planningTicketId = planningTicketId;
    }

    public BacklogSprint getSprint() {
        return sprint;
    }

    public void setSprint(BacklogSprint sprint) {
        this.sprint = sprint;
    }

    public List<BacklogSubtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<BacklogSubtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtask(BacklogSubtask subtask) {
        this.subtasks.add(subtask);
        subtask.setItem(this);
    }
}
