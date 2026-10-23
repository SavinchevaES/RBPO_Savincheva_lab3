package com.example.rbpo_savincheva.service;

import com.example.rbpo_savincheva.dto.Requests.UserRequest;
import com.example.rbpo_savincheva.entity.User;
import com.example.rbpo_savincheva.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
  private final UserRepository repository;
  private final VolunteerCleanupService cleanup;

  public UserService(UserRepository repository, VolunteerCleanupService cleanup) {
    this.repository = repository;
    this.cleanup = cleanup;
  }

  public List<User> findAll() {
    return repository.findAll();
  }

  public User find(long id) {
    return repository.findById(id).orElseThrow(() -> new NotFoundException("User", id));
  }

  public User create(UserRequest r) {
    validate(r.name(), r.email());
    User user = new User();
    user.setName(r.name());
    user.setEmail(r.email());
    return repository.saveAndFlush(user);
  }

  public User patch(long id, UserRequest r) {
    User user = find(id);
    String name = r.name() == null ? user.getName() : r.name();
    String email = r.email() == null ? user.getEmail() : r.email();
    validate(name, email);
    user.setName(name);
    user.setEmail(email);
    return repository.saveAndFlush(user);
  }

  public void delete(long id) {
    find(id);
    cleanup.deleteUser(id);
  }

  private void validate(String name, String email) {
    if (name == null || name.isBlank() || email == null || email.isBlank())
      throw new IllegalArgumentException("name and email are required");
  }
}
