package com.example.rbpo_savincheva.repository;

import com.example.rbpo_savincheva.entity.VolunteerResponse;
import com.example.rbpo_savincheva.entity.AdmissionStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerResponseRepository extends JpaRepository<VolunteerResponse, Long> {

  List<VolunteerResponse> findByTaskId(Long taskId);

  List<VolunteerResponse> findByVolunteerId(Long volunteerId);

  List<VolunteerResponse> findByTask_Campaign_Id(Long campaignId);

  boolean existsByTaskIdAndVolunteerId(Long taskId, Long volunteerId);

  long countByTaskIdAndAdmissionStatus(Long taskId, AdmissionStatus admissionStatus);
}
