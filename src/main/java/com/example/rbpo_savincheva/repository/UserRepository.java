package com.example.rbpo_savincheva.repository;

import com.example.rbpo_savincheva.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
