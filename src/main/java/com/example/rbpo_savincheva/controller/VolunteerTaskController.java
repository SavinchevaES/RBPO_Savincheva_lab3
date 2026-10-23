package com.example.rbpo_savincheva.controller;

import com.example.rbpo_savincheva.dto.Requests.TaskRequest;
import com.example.rbpo_savincheva.dto.Responses.TaskDto;
import com.example.rbpo_savincheva.dto.Requests.ApplyRequest;
import com.example.rbpo_savincheva.dto.Responses.VolunteerResponseDto;
import com.example.rbpo_savincheva.service.VolunteerResponseService;
import com.example.rbpo_savincheva.service.VolunteerTaskService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/volunteer-tasks")
public class VolunteerTaskController {
  private final VolunteerTaskService service;
  private final VolunteerResponseService responseService;

  public VolunteerTaskController(VolunteerTaskService service, VolunteerResponseService responseService) {
    this.service = service;
    this.responseService = responseService;
  }

  @GetMapping
  public List<TaskDto> all() {
    return service.findAll().stream().map(TaskDto::from).toList();
  }

  @GetMapping("/{id}")
  public TaskDto one(@PathVariable long id) {
    return TaskDto.from(service.find(id));
  }

  @PostMapping
  public ResponseEntity<TaskDto> create(@RequestBody TaskRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(TaskDto.from(service.create(request)));
  }

  @PatchMapping("/{id}")
  public TaskDto patch(@PathVariable long id, @RequestBody TaskRequest request) {
    return TaskDto.from(service.patch(id, request));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(id);
  }

  // Бизнес-операция 2: волонтёр откликается на задачу
  @PostMapping("/{id}/apply")
  public ResponseEntity<VolunteerResponseDto> apply(
      @PathVariable long id, @RequestBody ApplyRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(VolunteerResponseDto.from(responseService.apply(id, request)));
  }
}
