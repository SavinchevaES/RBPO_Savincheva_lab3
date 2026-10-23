package com.example.rbpo_savincheva.controller;

import com.example.rbpo_savincheva.dto.Requests.CampaignRequest;
import com.example.rbpo_savincheva.dto.Responses.CampaignDto;
import com.example.rbpo_savincheva.dto.Responses.CompletionDto;
import com.example.rbpo_savincheva.dto.Responses.VolunteerHoursDto;
import com.example.rbpo_savincheva.service.VolunteerCampaignService;
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
@RequestMapping("/api/volunteer-campaigns")
public class VolunteerCampaignController {
  private final VolunteerCampaignService service;

  public VolunteerCampaignController(VolunteerCampaignService service) {
    this.service = service;
  }

  @GetMapping
  public List<CampaignDto> all() {
    return service.findAll().stream().map(CampaignDto::from).toList();
  }

  @GetMapping("/{id}")
  public CampaignDto one(@PathVariable long id) {
    return CampaignDto.from(service.find(id));
  }

  @PostMapping
  public ResponseEntity<CampaignDto> create(@RequestBody CampaignRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(CampaignDto.from(service.create(request)));
  }

  @PatchMapping("/{id}")
  public CampaignDto patch(@PathVariable long id, @RequestBody CampaignRequest request) {
    return CampaignDto.from(service.patch(id, request));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(id);
  }

  // Бизнес-операция 1: запустить акцию
  @PostMapping("/{id}/activate")
  public CampaignDto activate(@PathVariable long id) {
    return CampaignDto.from(service.activate(id));
  }

  // Бизнес-операция 5: завершить акцию, вернуть часы волонтёров
  @PostMapping("/{id}/complete")
  public CompletionDto complete(@PathVariable long id) {
    VolunteerCampaignService.CompletionResult result = service.complete(id);
    List<VolunteerHoursDto> volunteers =
        result.confirmedHours().entrySet().stream()
            .map(
                e ->
                    new VolunteerHoursDto(
                        e.getKey().getId(), e.getKey().getName(), e.getValue()))
            .toList();
    return new CompletionDto(CampaignDto.from(result.campaign()), volunteers);
  }
}
