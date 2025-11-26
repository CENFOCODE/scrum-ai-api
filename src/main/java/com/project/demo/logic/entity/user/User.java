package com.project.demo.logic.entity.user;
import com.project.demo.logic.entity.achievements.Achievement;
import com.project.demo.logic.entity.feedback.Feedback;
import com.project.demo.logic.entity.history.History;
import com.project.demo.logic.entity.improvementPlan.ImprovementPlan;
import com.project.demo.logic.entity.notification.Notification;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.simulation.Simulation;
import com.project.demo.logic.entity.simulationUser.SimulationUser;
import com.project.demo.logic.entity.simulationMetric.SimulationMetric;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Table(name = "users")
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100, name = "last_name")
    private String lastname;

    @Column(unique = true, length = 150, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name = "google_account")
    private boolean googleAccount = false;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "auth_provider")
    private String authProvider = "local";

    @Column(name = "reset_token")
    private String resetToken;
    public User(String name, String lastname, String email, boolean googleAccount, String authProvider, String password, Role role) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.googleAccount = googleAccount;
        this.authProvider = authProvider;
        this.password = password;
        this.role = role;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
    }


    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;


    @OneToMany(mappedBy = "user")
    private List<Feedback> feedbackList;

    @OneToMany(mappedBy = "user")
    private List<SimulationMetric> metrics;

    @OneToMany(mappedBy = "user")
    private List<Achievement> achievements;

    @OneToMany(mappedBy = "user")
    private List<ImprovementPlan> improvementPlans;

    @OneToMany(mappedBy = "user")
    private List<History> historyList;


    // Constructors
    public User() {}

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Role getRole() {
        return role;
    }

    public User setRole(Role role) {
        this.role = role;

        return this;
    }

    public boolean isGoogleAccount() {
        return googleAccount;
    }

    public void setGoogleAccount(boolean googleAccount) {
        this.googleAccount = googleAccount;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }


    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<ImprovementPlan> getImprovementPlan() {
        return improvementPlans;
    }

    public void setImprovementPlan(List<ImprovementPlan> improvementPlan) {
        this.improvementPlans = improvementPlan;
    }


    public List<Feedback> getFeedbackList() {
        return feedbackList;
    }

    public void setFeedbackList(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    public List<SimulationMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<SimulationMetric> metrics) {
        this.metrics = metrics;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public List<ImprovementPlan> getImprovementPlans() {
        return improvementPlans;
    }

    public void setImprovementPlans(List<ImprovementPlan> improvementPlans) {
        this.improvementPlans = improvementPlans;
    }

    public List<History> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
    }
}