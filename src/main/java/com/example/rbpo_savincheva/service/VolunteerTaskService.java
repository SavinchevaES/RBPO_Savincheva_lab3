package com.example.rbpo_savincheva.service;

import com.example.rbpo_savincheva.dto.Requests.TaskRequest;
import com.example.rbpo_savincheva.entity.VolunteerTask;
import com.example.rbpo_savincheva.repository.VolunteerCampaignRepository;
import com.example.rbpo_savincheva.repository.VolunteerTaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VolunteerTaskService {
  private final VolunteerTaskRepository repository;
  private final VolunteerCampaignRepository campaigns;
  private final VolunteerCleanupService cleanup;

  public VolunteerTaskService(
      VolunteerTaskRepository repository,
      VolunteerCampaignRepository campaigns,
      VolunteerCleanupService cleanup) {
    this.repository = repository;
    this.campaigns = campaigns;
    this.cleanup = cleanup;
  }

  public List<VolunteerTask> findAll() {
    return repository.findAll();
  }

  public VolunteerTask find(long id) {
    return repository.findById(id).orElseThrow(() -> new NotFoundException("Volunteer task", id));
  }

  public VolunteerTask create(TaskRequest r) {
    validate(r);
    VolunteerTask task = new VolunteerTask();
    apply(task, r);
    return repository.saveAndFlush(task);
  }

  public VolunteerTask patch(long id, TaskRequest r) {
    VolunteerTask o = find(id);
    TaskRequest m =
        new TaskRequest(
            r.campaignId() == null ? o.getCampaign().getId() : r.campaignId(),
            r.requiredParticipants() == null ? o.getRequiredParticipants() : r.requiredParticipants(),
            r.startAt() == null ? o.getStartAt() : r.startAt(),
            r.durationHours() == null ? o.getDurationHours() : r.durationHours());
    validate(m);
    apply(o, m);
    return repository.saveAndFlush(o);
  }

  public void delete(long id) {
    find(id);
    cleanup.deleteTask(id);
  }

  private void apply(VolunteerTask task, TaskRequest r) {
    task.setCampaign(
        campaigns
            .findById(r.campaignId())
            .orElseThrow(() -> new NotFoundException("Volunteer campaign", r.campaignId())));
    task.setRequiredParticipants(r.requiredParticipants());
    task.setStartAt(r.startAt());
    task.setDurationHours(r.durationHours());
  }

  private void validate(TaskRequest r) {
    if (r.campaignId() == null
        || r.requiredParticipants() == null
        || r.requiredParticipants() < 1
        || r.startAt() == null
        || r.durationHours() == null
        || r.durationHours() < 1)
      throw new IllegalArgumentException(
          "campaignId, positive requiredParticipants, startAt and positive durationHours are"
              + " required");
  }
}
