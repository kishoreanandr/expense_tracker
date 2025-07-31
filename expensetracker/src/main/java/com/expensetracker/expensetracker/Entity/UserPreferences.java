package com.expensetracker.expensetracker.Entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_frequency")
    private NotificationFrequency notificationFrequency = NotificationFrequency.WEEKLY;

    @Column(name = "over_budget_alerts")
    private Boolean overBudgetAlerts = true;

    @Column(name = "email_notifications")
    private Boolean emailNotifications = true;

    @Column(name = "currency")
    private String currency = "INR";

    @Column(name = "timezone")
    private String timezone = "Asia/Kolkata";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    public enum NotificationFrequency {
        NONE, DAILY, WEEKLY, MONTHLY, YEARLY
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public UserPreferences() {}

    public UserPreferences(User user) {
        this.user = user;
    }

    public UserPreferences(NotificationFrequency notificationFrequency, Boolean overBudgetAlerts,
                           Boolean emailNotifications, String currency, String timezone, User user) {
        this.notificationFrequency = notificationFrequency;
        this.overBudgetAlerts = overBudgetAlerts;
        this.emailNotifications = emailNotifications;
        this.currency = currency;
        this.timezone = timezone;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationFrequency getNotificationFrequency() {
        return notificationFrequency;
    }

    public void setNotificationFrequency(NotificationFrequency notificationFrequency) {
        this.notificationFrequency = notificationFrequency;
    }

    public Boolean getOverBudgetAlerts() {
        return overBudgetAlerts;
    }

    public void setOverBudgetAlerts(Boolean overBudgetAlerts) {
        this.overBudgetAlerts = overBudgetAlerts;
    }

    public Boolean getEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}