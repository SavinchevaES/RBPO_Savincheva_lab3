package com.example.rbpo_savincheva.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "volunteer_tasks")
public class VolunteerTask {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "campaign_id", nullable = false)
  private VolunteerCampaign campaign;

  @Column(nullable = false)
  private int requiredParticipants;

  @Column(nullable = false)
  private LocalDateTime startAt;

  @Column(nullable = false)
  private int durationHours;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
    updatedAt = createdAt;
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public VolunteerCampaign getCampaign() {
    return campaign;
  }

  public void setCampaign(VolunteerCampaign campaign) {
    this.campaign = campaign;
  }

  public int getRequiredParticipants() {
    return requiredParticipants;
  }

  public void setRequiredParticipants(int requiredParticipants) {
    this.requiredParticipants = requiredParticipants;
  }

  public LocalDateTime getStartAt() {
    return startAt;
  }

  public void setStartAt(LocalDateTime startAt) {
    this.startAt = startAt;
  }

  public int getDurationHours() {
    return durationHours;
  }

  public void setDurationHours(int durationHours) {
    this.durationHours = durationHours;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

}
