package com.example.rbpo_savincheva.config;

import com.example.rbpo_savincheva.entity.AdmissionStatus;
import com.example.rbpo_savincheva.entity.CampaignStatus;
import com.example.rbpo_savincheva.entity.User;
import com.example.rbpo_savincheva.entity.VolunteerCampaign;
import com.example.rbpo_savincheva.entity.VolunteerResponse;
import com.example.rbpo_savincheva.entity.VolunteerTask;
import com.example.rbpo_savincheva.repository.UserRepository;
import com.example.rbpo_savincheva.repository.VolunteerCampaignRepository;
import com.example.rbpo_savincheva.repository.VolunteerResponseRepository;
import com.example.rbpo_savincheva.repository.VolunteerTaskRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Тестовые данные: добавляются один раз, если таблица users пустая
@Component
public class DataInitializer implements CommandLineRunner {
  private final UserRepository users;
  private final VolunteerCampaignRepository campaigns;
  private final VolunteerTaskRepository tasks;
  private final VolunteerResponseRepository responses;

  public DataInitializer(
      UserRepository users,
      VolunteerCampaignRepository campaigns,
      VolunteerTaskRepository tasks,
      VolunteerResponseRepository responses) {
    this.users = users;
    this.campaigns = campaigns;
    this.tasks = tasks;
    this.responses = responses;
  }

  @Override
  @Transactional
  public void run(String... args) {
    if (users.count() > 0) {
      return;
    }

    User anna = user("Анна Координатор", "anna@example.com");
    User ivan = user("Иван Петров", "ivan@example.com");
    User petr = user("Пётр Сидоров", "petr@example.com");
    user("Ольга Смирнова", "olga@example.com");
    user("Сергей Волков", "sergey@example.com");

    VolunteerCampaign shelter =
        campaign("Помощь приюту для животных", LocalDate.of(2030, 5, 1), LocalDate.of(2030, 5, 31),
            anna, CampaignStatus.PLANNED);
    VolunteerCampaign park =
        campaign("Уборка городского парка", LocalDate.of(2030, 6, 1), LocalDate.of(2030, 6, 30),
            anna, CampaignStatus.ACTIVE);

    task(shelter, 2, LocalDateTime.of(2030, 5, 10, 10, 0), 3);
    VolunteerTask cleaning = task(park, 2, LocalDateTime.of(2030, 6, 5, 9, 0), 4);
    task(park, 3, LocalDateTime.of(2030, 6, 12, 9, 0), 2);

    response(cleaning, ivan);
    response(cleaning, petr);
  }

  private User user(String name, String email) {
    User user = new User();
    user.setName(name);
    user.setEmail(email);
    return users.save(user);
  }

  private VolunteerCampaign campaign(
      String title, LocalDate start, LocalDate end, User coordinator, CampaignStatus status) {
    VolunteerCampaign campaign = new VolunteerCampaign();
    campaign.setTitle(title);
    campaign.setStartDate(start);
    campaign.setEndDate(end);
    campaign.setCoordinator(coordinator);
    campaign.setStatus(status);
    return campaigns.save(campaign);
  }

  private VolunteerTask task(
      VolunteerCampaign campaign, int required, LocalDateTime startAt, int hours) {
    VolunteerTask task = new VolunteerTask();
    task.setCampaign(campaign);
    task.setRequiredParticipants(required);
    task.setStartAt(startAt);
    task.setDurationHours(hours);
    return tasks.save(task);
  }

  private void response(VolunteerTask task, User volunteer) {
    VolunteerResponse response = new VolunteerResponse();
    response.setTask(task);
    response.setVolunteer(volunteer);
    response.setAdmissionStatus(AdmissionStatus.PENDING);
    responses.save(response);
  }
}
