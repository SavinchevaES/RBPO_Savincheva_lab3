package com.example.rbpo_savincheva.service;

import com.example.rbpo_savincheva.dto.Requests.ReportRequest;
import com.example.rbpo_savincheva.dto.Requests.SubmitReportRequest;
import com.example.rbpo_savincheva.entity.AdmissionStatus;
import com.example.rbpo_savincheva.entity.CampaignStatus;
import com.example.rbpo_savincheva.entity.ConfirmationStatus;
import com.example.rbpo_savincheva.entity.ParticipationReport;
import com.example.rbpo_savincheva.entity.VolunteerResponse;
import com.example.rbpo_savincheva.entity.VolunteerTask;
import com.example.rbpo_savincheva.repository.ParticipationReportRepository;
import com.example.rbpo_savincheva.repository.VolunteerResponseRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParticipationReportService {
  private final ParticipationReportRepository repository;
  private final VolunteerResponseRepository responses;
  private final VolunteerCleanupService cleanup;

  public ParticipationReportService(
      ParticipationReportRepository repository,
      VolunteerResponseRepository responses,
      VolunteerCleanupService cleanup) {
    this.repository = repository;
    this.responses = responses;
    this.cleanup = cleanup;
  }

  public List<ParticipationReport> findAll() {
    return repository.findAll();
  }

  public ParticipationReport find(long id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Participation report", id));
  }

  public ParticipationReport create(ReportRequest r) {
    validate(r);
    ParticipationReport report = new ParticipationReport();
    apply(report, r);
    return repository.saveAndFlush(report);
  }

  public ParticipationReport patch(long id, ReportRequest r) {
    ParticipationReport o = find(id);
    ReportRequest m =
        new ReportRequest(
            r.responseId() == null ? o.getResponse().getId() : r.responseId(),
            r.actualHours() == null ? o.getActualHours() : r.actualHours(),
            r.confirmationStatus() == null
                ? o.getConfirmationStatus().name()
                : r.confirmationStatus());
    validate(m);
    apply(o, m);
    return repository.saveAndFlush(o);
  }

  public void delete(long id) {
    find(id);
    cleanup.deleteReport(id);
  }

  // ---------- Бизнес-операция 4: подать отчёт об участии (отклик + задача + акция) ----------
  public ParticipationReport submit(long responseId, SubmitReportRequest r) {
    if (r == null || r.actualHours() == null || r.actualHours() < 0) {
      throw new IllegalArgumentException("non-negative actualHours is required");
    }

    VolunteerResponse response =
        responses
            .findById(responseId)
            .orElseThrow(() -> new NotFoundException("Volunteer response", responseId));
    VolunteerTask task = response.getTask();

    if (response.getAdmissionStatus() != AdmissionStatus.APPROVED) {
      throw new ConflictException("A report can be submitted only by an approved volunteer");
    }
    if (task.getCampaign().getStatus() != CampaignStatus.ACTIVE) {
      throw new ConflictException("Reports are accepted only for active campaigns");
    }
    if (repository.existsByResponseId(responseId)) {
      throw new ConflictException("A report for this response already exists");
    }
    if (r.actualHours() > task.getDurationHours()) {
      throw new ConflictException("Actual hours cannot exceed the task duration");
    }

    ParticipationReport report = new ParticipationReport();
    report.setResponse(response);
    report.setActualHours(r.actualHours());
    report.setConfirmationStatus(ConfirmationStatus.PENDING);
    return repository.saveAndFlush(report);
  }

  private void apply(ParticipationReport report, ReportRequest r) {
    report.setResponse(
        responses
            .findById(r.responseId())
            .orElseThrow(() -> new NotFoundException("Volunteer response", r.responseId())));
    report.setActualHours(r.actualHours());
    report.setConfirmationStatus(ConfirmationStatus.parse(r.confirmationStatus()));
  }

  private void validate(ReportRequest r) {
    if (r.responseId() == null
        || r.actualHours() == null
        || r.actualHours() < 0
        || r.confirmationStatus() == null
        || r.confirmationStatus().isBlank())
      throw new IllegalArgumentException(
          "responseId, non-negative actualHours and confirmationStatus are required");
    ConfirmationStatus.parse(r.confirmationStatus());
  }
}
