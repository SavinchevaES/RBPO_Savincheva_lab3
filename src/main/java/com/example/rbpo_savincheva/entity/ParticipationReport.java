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
@Table(name = "participation_reports")
public class ParticipationReport {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "response_id", nullable = false, unique = true)
  private VolunteerResponse response;

  @Column(nullable = false)
  private int actualHours;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ConfirmationStatus confirmationStatus;

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

  public VolunteerResponse getResponse() {
    return response;
  }

  public void setResponse(VolunteerResponse response) {
    this.response = response;
  }

  public int getActualHours() {
    return actualHours;
  }

  public void setActualHours(int actualHours) {
    this.actualHours = actualHours;
  }

  public ConfirmationStatus getConfirmationStatus() {
    return confirmationStatus;
  }

  public void setConfirmationStatus(ConfirmationStatus confirmationStatus) {
    this.confirmationStatus = confirmationStatus;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

}
