package com.example.rbpo_savincheva.repository;

import com.example.rbpo_savincheva.entity.VolunteerCampaign;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerCampaignRepository extends JpaRepository<VolunteerCampaign, Long> {

  List<VolunteerCampaign> findByCoordinatorId(Long coordinatorId);

  // Блокирует строку акции до конца транзакции
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select c from VolunteerCampaign c where c.id = :id")
  Optional<VolunteerCampaign> findByIdForUpdate(@Param("id") Long id);
}
