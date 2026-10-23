package com.example.rbpo_savincheva.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class Requests {
  private Requests() {}

  public record UserRequest(String name, String email) {}

  public record CampaignRequest(
      String title, LocalDate startDate, LocalDate endDate, Long coordinatorId, String status) {}

  public record TaskRequest(
      Long campaignId,
      Integer requiredParticipants,
      LocalDateTime startAt,
      Integer durationHours) {}

  public record ResponseRequest(Long taskId, Long volunteerId, String admissionStatus) {}

  public record ReportRequest(Long responseId, Integer actualHours, String confirmationStatus) {}

  // Бизнес-операции
  public record ApplyRequest(Long volunteerId) {}

  public record SubmitReportRequest(Integer actualHours) {}
}
