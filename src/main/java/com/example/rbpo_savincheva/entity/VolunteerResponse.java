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
@Table(
    name = "volunteer_responses",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_responses_task_volunteer",
            columnNames = {"task_id", "volunteer_id"}))
public class VolunteerResponse {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "task_id", nullable = false)
  private VolunteerTask task;

  @ManyToOne(optional = false)
  @JoinColumn(name = "volunteer_id", nullable = false)
  private User volunteer;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private AdmissionStatus admissionStatus;

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

  public VolunteerTask getTask() {
    return task;
  }

  public void setTask(VolunteerTask task) {
    this.task = task;
  }

  public User getVolunteer() {
    return volunteer;
  }

  public void setVolunteer(User volunteer) {
    this.volunteer = volunteer;
  }

  public AdmissionStatus getAdmissionStatus() {
    return admissionStatus;
  }

  public void setAdmissionStatus(AdmissionStatus admissionStatus) {
    this.admissionStatus = admissionStatus;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

}
