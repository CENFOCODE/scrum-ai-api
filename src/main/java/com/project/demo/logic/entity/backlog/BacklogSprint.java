package com.project.demo.logic.entity.backlog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "backlog_sprint")
public class BacklogSprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String goal;

    private String dates;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private BacklogSprint parent;

    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<BacklogItem> items = new ArrayList<>();

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public String getDates() { return dates; }
    public void setDates(String dates) { this.dates = dates; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BacklogSprint getParent() { return parent; }
    public void setParent(BacklogSprint parent) { this.parent = parent; }

    public List<BacklogItem> getItems() { return items; }

    public void addItem(BacklogItem item) {
        items.add(item);
        item.setSprint(this);
    }

    public void removeItem(BacklogItem item) {
        items.remove(item);
        item.setSprint(null);
    }

    public void setItems(List<BacklogItem> clonedItems) {
        this.items.clear();
        if (clonedItems != null) {
            for (BacklogItem it : clonedItems) {
                this.addItem(it);
            }
        }
    }
}
