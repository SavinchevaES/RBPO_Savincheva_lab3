package com.example.rbpo_savincheva.repository;

import com.example.rbpo_savincheva.entity.ParticipationReport;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationReportRepository extends JpaRepository<ParticipationReport, Long> {

  List<ParticipationReport> findByResponseId(Long responseId);

  List<ParticipationReport> findByResponse_Task_Campaign_Id(Long campaignId);

  boolean existsByResponseId(Long responseId);
}
