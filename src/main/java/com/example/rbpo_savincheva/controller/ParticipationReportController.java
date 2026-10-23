package com.example.rbpo_savincheva.controller;

import com.example.rbpo_savincheva.dto.Requests.ReportRequest;
import com.example.rbpo_savincheva.dto.Responses.ReportDto;
import com.example.rbpo_savincheva.service.ParticipationReportService;
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
@RequestMapping("/api/participation-reports")
public class ParticipationReportController {
  private final ParticipationReportService service;

  public ParticipationReportController(ParticipationReportService service) {
    this.service = service;
  }

  @GetMapping
  public List<ReportDto> all() {
    return service.findAll().stream().map(ReportDto::from).toList();
  }

  @GetMapping("/{id}")
  public ReportDto one(@PathVariable long id) {
    return ReportDto.from(service.find(id));
  }

  @PostMapping
  public ResponseEntity<ReportDto> create(@RequestBody ReportRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(ReportDto.from(service.create(request)));
  }

  @PatchMapping("/{id}")
  public ReportDto patch(@PathVariable long id, @RequestBody ReportRequest request) {
    return ReportDto.from(service.patch(id, request));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(id);
  }
}
