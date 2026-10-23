package com.example.rbpo_savincheva.repository;

import com.example.rbpo_savincheva.entity.VolunteerTask;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerTaskRepository extends JpaRepository<VolunteerTask, Long> {

  List<VolunteerTask> findByCampaignId(Long campaignId);

  boolean existsByCampaignId(Long campaignId);

  // Блокирует строку задачи: два одновременных одобрения не займут одно место дважды
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select t from VolunteerTask t where t.id = :id")
  Optional<VolunteerTask> findByIdForUpdate(@Param("id") Long id);
}
