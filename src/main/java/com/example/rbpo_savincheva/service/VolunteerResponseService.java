package com.example.rbpo_savincheva.service;

import com.example.rbpo_savincheva.dto.Requests.ApplyRequest;
import com.example.rbpo_savincheva.dto.Requests.ResponseRequest;
import com.example.rbpo_savincheva.entity.AdmissionStatus;
import com.example.rbpo_savincheva.entity.CampaignStatus;
import com.example.rbpo_savincheva.entity.User;
import com.example.rbpo_savincheva.entity.VolunteerCampaign;
import com.example.rbpo_savincheva.entity.VolunteerResponse;
import com.example.rbpo_savincheva.entity.VolunteerTask;
import com.example.rbpo_savincheva.repository.UserRepository;
import com.example.rbpo_savincheva.repository.VolunteerResponseRepository;
import com.example.rbpo_savincheva.repository.VolunteerTaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VolunteerResponseService {
  private final VolunteerResponseRepository repository;
  private final VolunteerTaskRepository tasks;
  private final UserRepository users;
  private final VolunteerCleanupService cleanup;

  public VolunteerResponseService(
      VolunteerResponseRepository repository,
      VolunteerTaskRepository tasks,
      UserRepository users,
      VolunteerCleanupService cleanup) {
    this.repository = repository;
    this.tasks = tasks;
    this.users = users;
    this.cleanup = cleanup;
  }

  public List<VolunteerResponse> findAll() {
    return repository.findAll();
  }

  public VolunteerResponse find(long id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Volunteer response", id));
  }

  public VolunteerResponse create(ResponseRequest r) {
    validate(r);
    VolunteerResponse response = new VolunteerResponse();
    apply(response, r);
    return repository.saveAndFlush(response);
  }

  public VolunteerResponse patch(long id, ResponseRequest r) {
    VolunteerResponse o = find(id);
    ResponseRequest m =
        new ResponseRequest(
            r.taskId() == null ? o.getTask().getId() : r.taskId(),
            r.volunteerId() == null ? o.getVolunteer().getId() : r.volunteerId(),
            r.admissionStatus() == null ? o.getAdmissionStatus().name() : r.admissionStatus());
    validate(m);
    apply(o, m);
    return repository.saveAndFlush(o);
  }

  public void delete(long id) {
    find(id);
    cleanup.deleteResponse(id);
  }

  // ---------- Бизнес-операция 2: волонтёр откликается на задачу (задача + акция + пользователь) ----------
  public VolunteerResponse apply(long taskId, ApplyRequest r) {
    if (r == null || r.volunteerId() == null) {
      throw new IllegalArgumentException("volunteerId is required");
    }

    VolunteerTask task =
        tasks
            .findByIdForUpdate(taskId)
            .orElseThrow(() -> new NotFoundException("Volunteer task", taskId));
    User volunteer =
        users
            .findById(r.volunteerId())
            .orElseThrow(() -> new NotFoundException("User", r.volunteerId()));
    VolunteerCampaign campaign = task.getCampaign();

    if (campaign.getStatus() != CampaignStatus.ACTIVE) {
      throw new ConflictException("Responses are accepted only for active campaigns");
    }
    // текущее время берётся на сервере
    if (!task.getStartAt().isAfter(LocalDateTime.now())) {
      throw new ConflictException("The task has already started");
    }
    if (campaign.getCoordinator().getId().equals(volunteer.getId())) {
      throw new ConflictException("The coordinator cannot respond to a task of their own campaign");
    }
    if (repository.existsByTaskIdAndVolunteerId(taskId, volunteer.getId())) {
      throw new ConflictException("The volunteer has already responded to this task");
    }

    VolunteerResponse response = new VolunteerResponse();
    response.setTask(task);
    response.setVolunteer(volunteer);
    response.setAdmissionStatus(AdmissionStatus.PENDING);
    return repository.saveAndFlush(response);
  }

  // ---------- Бизнес-операция 3: допустить волонтёра (отклик + задача + акция) ----------
  public VolunteerResponse approve(long id) {
    VolunteerResponse response = find(id);

    // блокируем задачу, чтобы два одобрения не заняли одно место
    VolunteerTask task =
        tasks
            .findByIdForUpdate(response.getTask().getId())
            .orElseThrow(() -> new NotFoundException("Volunteer task", response.getTask().getId()));

    if (task.getCampaign().getStatus() != CampaignStatus.ACTIVE) {
      throw new ConflictException("Responses can be approved only in active campaigns");
    }
    if (response.getAdmissionStatus() != AdmissionStatus.PENDING) {
      throw new ConflictException("Only pending responses can be approved");
    }

    long approved =
        repository.countByTaskIdAndAdmissionStatus(task.getId(), AdmissionStatus.APPROVED);
    if (approved >= task.getRequiredParticipants()) {
      throw new ConflictException("No free places left for this task");
    }

    response.setAdmissionStatus(AdmissionStatus.APPROVED);
    return repository.saveAndFlush(response);
  }

  private void apply(VolunteerResponse response, ResponseRequest r) {
    response.setTask(
        tasks
            .findById(r.taskId())
            .orElseThrow(() -> new NotFoundException("Volunteer task", r.taskId())));
    response.setVolunteer(
        users
            .findById(r.volunteerId())
            .orElseThrow(() -> new NotFoundException("User", r.volunteerId())));
    response.setAdmissionStatus(AdmissionStatus.parse(r.admissionStatus()));
  }

  private void validate(ResponseRequest r) {
    if (r.taskId() == null
        || r.volunteerId() == null
        || r.admissionStatus() == null
        || r.admissionStatus().isBlank())
      throw new IllegalArgumentException("taskId, volunteerId and admissionStatus are required");
    AdmissionStatus.parse(r.admissionStatus());
  }
}
