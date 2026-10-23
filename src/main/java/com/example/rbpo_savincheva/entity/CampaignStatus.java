package com.example.rbpo_savincheva.entity;

import java.util.Arrays;

public enum CampaignStatus {
  PLANNED,
  ACTIVE,
  COMPLETED,
  CANCELLED;

  public static CampaignStatus parse(String value) {
    for (CampaignStatus status : values()) {
      if (status.name().equalsIgnoreCase(value.trim())) {
        return status;
      }
    }
    throw new IllegalArgumentException(
        "Unknown campaign status: " + value + ". Allowed: " + Arrays.toString(values()));
  }
}
