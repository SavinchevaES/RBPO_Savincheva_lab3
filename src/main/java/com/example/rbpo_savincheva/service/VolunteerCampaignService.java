package com.example.rbpo_savincheva.service;

import com.example.rbpo_savincheva.dto.Requests.CampaignRequest;
import com.example.rbpo_savincheva.entity.AdmissionStatus;
import com.example.rbpo_savincheva.entity.CampaignStatus;
import com.example.rbpo_savincheva.entity.ConfirmationStatus;
import com.example.rbpo_savincheva.entity.ParticipationReport;
import com.example.rbpo_savincheva.entity.User;
import com.example.rbpo_savincheva.entity.VolunteerCampaign;
import com.example.rbpo_savincheva.entity.VolunteerResponse;
import com.example.rbpo_savincheva.repository.ParticipationReportRepository;
import com.example.rbpo_savincheva.repository.UserRepository;
import com.example.rbpo_savincheva.repository.VolunteerCampaignRepository;
import com.example.rbpo_savincheva.repository.VolunteerResponseRepository;
import com.example.rbpo_savincheva.repository.VolunteerTaskRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VolunteerCampaignService {
  private final VolunteerCampaignRepository repository;
  private final UserRepository users;
  private final VolunteerTaskRepository tasks;
  private final VolunteerResponseRepository responses;
  private final ParticipationReportRepository reports;
  private final VolunteerCleanupService cleanup;

  public VolunteerCampaignService(
      VolunteerCampaignRepository repository,
      UserRepository users,
      VolunteerTaskRepository tasks,
      VolunteerResponseRepository responses,
      ParticipationReportRepository reports,
      VolunteerCleanupService cleanup) {
    this.repository = repository;
    this.users = users;
    this.tasks = tasks;
    this.responses = responses;
    this.reports = reports;
    this.cleanup = cleanup;
  }

  // Итог завершения акции: акция + подтверждённые часы по каждому волонтёру
  public record CompletionResult(VolunteerCampaign campaign, Map<User, Integer> confirmedHours) {}

  public List<VolunteerCampaign> findAll() {
    return repository.findAll();
  }

  public VolunteerCampaign find(long id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Volunteer campaign", id));
  }

  public VolunteerCampaign create(CampaignRequest r) {
    validate(r);
    VolunteerCampaign campaign = new VolunteerCampaign();
    apply(campaign, r);
    return repository.saveAndFlush(campaign);
  }

  public VolunteerCampaign patch(long id, CampaignRequest r) {
    VolunteerCampaign o = find(id);
    CampaignRequest merged =
        new CampaignRequest(
            r.title() == null ? o.getTitle() : r.title(),
            r.startDate() == null ? o.getStartDate() : r.startDate(),
            r.endDate() == null ? o.getEndDate() : r.endDate(),
            r.coordinatorId() == null ? o.getCoordinator().getId() : r.coordinatorId(),
            r.status() == null ? o.getStatus().name() : r.status());
    validate(merged);
    apply(o, merged);
    return repository.saveAndFlush(o);
  }

  public void delete(long id) {
    find(id);
    cleanup.deleteCampaign(id);
  }

  // ---------- Бизнес-операция 1: запустить акцию (акция + задачи) ----------
  public VolunteerCampaign activate(long id) {
    VolunteerCampaign campaign = findForUpdate(id);

    if (campaign.getStatus() != CampaignStatus.PLANNED) {
      throw new ConflictException("Only a planned campaign can be activated");
    }
    if (!tasks.existsByCampaignId(id)) {
      throw new ConflictException("A campaign without tasks cannot be activated");
    }

    campaign.setStatus(CampaignStatus.ACTIVE);
    return repository.saveAndFlush(campaign);
  }

  // ---------- Бизнес-операция 5: завершить акцию (акция + отклики + отчёты) ----------
  public CompletionResult complete(long id) {
    VolunteerCampaign campaign = findForUpdate(id);

    if (campaign.getStatus() != CampaignStatus.ACTIVE) {
      throw new ConflictException("Only an active campaign can be completed");
    }

    List<VolunteerResponse> campaignResponses = responses.findByTask_Campaign_Id(id);
    List<ParticipationReport> campaignReports = reports.findByResponse_Task_Campaign_Id(id);

    Set<Long> reportedResponseIds =
        campaignReports.stream().map(rep -> rep.getResponse().getId()).collect(Collectors.toSet());

    // у каждого допущенного волонтёра должен быть отчёт об участии
    for (VolunteerResponse response : campaignResponses) {
      if (response.getAdmissionStatus() == AdmissionStatus.APPROVED
          && !reportedResponseIds.contains(response.getId())) {
        throw new ConflictException(
            "Approved volunteer response " + response.getId() + " has no participation report");
      }
    }

    // необработанные отклики автоматически отклоняются
    for (VolunteerResponse response : campaignResponses) {
      if (response.getAdmissionStatus() == AdmissionStatus.PENDING) {
        response.setAdmissionStatus(AdmissionStatus.REJECTED);
      }
    }
    responses.saveAll(campaignResponses);

    // отчёты подтверждаются, считаем часы по волонтёрам
    Map<User, Integer> hours = new LinkedHashMap<>();
    for (ParticipationReport report : campaignReports) {
      if (report.getConfirmationStatus() == ConfirmationStatus.PENDING) {
        report.setConfirmationStatus(ConfirmationStatus.CONFIRMED);
      }
      if (report.getConfirmationStatus() == ConfirmationStatus.CONFIRMED) {
        hours.merge(report.getResponse().getVolunteer(), report.getActualHours(), Integer::sum);
      }
    }
    reports.saveAll(campaignReports);

    campaign.setStatus(CampaignStatus.COMPLETED);
    return new CompletionResult(repository.saveAndFlush(campaign), hours);
  }

  private VolunteerCampaign findForUpdate(long id) {
    return repository
        .findByIdForUpdate(id)
        .orElseThrow(() -> new NotFoundException("Volunteer campaign", id));
  }

  private void apply(VolunteerCampaign campaign, CampaignRequest r) {
    campaign.setTitle(r.title());
    campaign.setStartDate(r.startDate());
    campaign.setEndDate(r.endDate());
    campaign.setCoordinator(
        users
            .findById(r.coordinatorId())
            .orElseThrow(() -> new NotFoundException("User", r.coordinatorId())));
    campaign.setStatus(CampaignStatus.parse(r.status()));
  }

  private void validate(CampaignRequest r) {
    if (r.title() == null
        || r.title().isBlank()
        || r.startDate() == null
        || r.endDate() == null
        || r.status() == null
        || r.status().isBlank()
        || r.coordinatorId() == null)
      throw new IllegalArgumentException("title, dates, coordinatorId and status are required");
    if (r.endDate().isBefore(r.startDate()))
      throw new IllegalArgumentException("endDate must not be before startDate");
    CampaignStatus.parse(r.status());
  }
}
