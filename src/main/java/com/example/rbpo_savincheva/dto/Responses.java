package com.example.rbpo_savincheva.dto;

import com.example.rbpo_savincheva.entity.ParticipationReport;
import com.example.rbpo_savincheva.entity.User;
import com.example.rbpo_savincheva.entity.VolunteerCampaign;
import com.example.rbpo_savincheva.entity.VolunteerResponse;
import com.example.rbpo_savincheva.entity.VolunteerTask;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Формат ответов API (такой же, как был в Задании 2)
public final class Responses {
  private Responses() {}

  public record UserDto(Long id, String name, String email, Instant createdAt) {
    public static UserDto from(User u) {
      return new UserDto(u.getId(), u.getName(), u.getEmail(), u.getCreatedAt());
    }
  }

  public record CampaignDto(
      Long id,
      String title,
      LocalDate startDate,
      LocalDate endDate,
      Long coordinatorId,
      String status,
      Instant createdAt,
      Instant updatedAt) {
    public static CampaignDto from(VolunteerCampaign c) {
      return new CampaignDto(
          c.getId(),
          c.getTitle(),
          c.getStartDate(),
          c.getEndDate(),
          c.getCoordinator().getId(),
          c.getStatus().name(),
          c.getCreatedAt(),
          c.getUpdatedAt());
    }
  }

  public record TaskDto(
      Long id,
      Long campaignId,
      int requiredParticipants,
      LocalDateTime startAt,
      int durationHours,
      Instant createdAt,
      Instant updatedAt) {
    public static TaskDto from(VolunteerTask t) {
      return new TaskDto(
          t.getId(),
          t.getCampaign().getId(),
          t.getRequiredParticipants(),
          t.getStartAt(),
          t.getDurationHours(),
          t.getCreatedAt(),
          t.getUpdatedAt());
    }
  }

  public record VolunteerResponseDto(
      Long id,
      Long taskId,
      Long volunteerId,
      String admissionStatus,
      Instant createdAt,
      Instant updatedAt) {
    public static VolunteerResponseDto from(VolunteerResponse r) {
      return new VolunteerResponseDto(
          r.getId(),
          r.getTask().getId(),
          r.getVolunteer().getId(),
          r.getAdmissionStatus().name(),
          r.getCreatedAt(),
          r.getUpdatedAt());
    }
  }

  public record ReportDto(
      Long id,
      Long responseId,
      int actualHours,
      String confirmationStatus,
      Instant createdAt,
      Instant updatedAt) {
    public static ReportDto from(ParticipationReport r) {
      return new ReportDto(
          r.getId(),
          r.getResponse().getId(),
          r.getActualHours(),
          r.getConfirmationStatus().name(),
          r.getCreatedAt(),
          r.getUpdatedAt());
    }
  }

  // Итог завершения акции: часы волонтёров
  public record VolunteerHoursDto(Long volunteerId, String volunteerName, int confirmedHours) {}

  public record CompletionDto(CampaignDto campaign, List<VolunteerHoursDto> volunteers) {}
}
