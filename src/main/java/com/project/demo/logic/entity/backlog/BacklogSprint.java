package com.project.demo.logic.entity.backlog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "backlog_sprint")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class BacklogSprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String goal;

    @Column(length = 255)
    private String dates;

    @Column(length = 20)
    private String startDate;

    @Column(length = 20)
    private String startTime;

    @Column(length = 20)
    private String endDate;

    @Column(length = 20)
    private String endTime;

    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BacklogItem> items = new ArrayList<>();

    public BacklogSprint() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<BacklogItem> getItems() {
        return items;
    }

    public void setItems(List<BacklogItem> items) {
        this.items = items;
    }

    public void addItem(BacklogItem item) {
        this.items.add(item);
        item.setSprint(this);
    }

    public void removeItem(BacklogItem item) {
        this.items.remove(item);
        item.setSprint(null);
    }
}
