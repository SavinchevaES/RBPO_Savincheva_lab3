package com.example.rbpo_savincheva.service;

import com.example.rbpo_savincheva.repository.ParticipationReportRepository;
import com.example.rbpo_savincheva.repository.UserRepository;
import com.example.rbpo_savincheva.repository.VolunteerCampaignRepository;
import com.example.rbpo_savincheva.repository.VolunteerResponseRepository;
import com.example.rbpo_savincheva.repository.VolunteerTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Каскадное удаление: сначала удаляются зависимые объекты, чтобы не остались битые ссылки
@Service
@Transactional
public class VolunteerCleanupService {
  private final UserRepository users;
  private final VolunteerCampaignRepository campaigns;
  private final VolunteerTaskRepository tasks;
  private final VolunteerResponseRepository responses;
  private final ParticipationReportRepository reports;

  public VolunteerCleanupService(
      UserRepository users,
      VolunteerCampaignRepository campaigns,
      VolunteerTaskRepository tasks,
      VolunteerResponseRepository responses,
      ParticipationReportRepository reports) {
    this.users = users;
    this.campaigns = campaigns;
    this.tasks = tasks;
    this.responses = responses;
    this.reports = reports;
  }

  public void deleteUser(long id) {
    campaigns.findByCoordinatorId(id).forEach(c -> deleteCampaign(c.getId()));
    responses.findByVolunteerId(id).forEach(r -> deleteResponse(r.getId()));
    users.deleteById(id);
  }

  public void deleteCampaign(long id) {
    tasks.findByCampaignId(id).forEach(t -> deleteTask(t.getId()));
    campaigns.deleteById(id);
  }

  public void deleteTask(long id) {
    responses.findByTaskId(id).forEach(r -> deleteResponse(r.getId()));
    tasks.deleteById(id);
  }

  public void deleteResponse(long id) {
    reports.deleteAll(reports.findByResponseId(id));
    responses.deleteById(id);
  }

  public void deleteReport(long id) {
    reports.deleteById(id);
  }
}
