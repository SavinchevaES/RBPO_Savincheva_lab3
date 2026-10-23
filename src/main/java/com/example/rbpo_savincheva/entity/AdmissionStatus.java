package com.example.rbpo_savincheva.entity;

import java.util.Arrays;

public enum AdmissionStatus {
  PENDING,
  APPROVED,
  REJECTED;

  public static AdmissionStatus parse(String value) {
    for (AdmissionStatus status : values()) {
      if (status.name().equalsIgnoreCase(value.trim())) {
        return status;
      }
    }
    throw new IllegalArgumentException(
        "Unknown admission status: " + value + ". Allowed: " + Arrays.toString(values()));
  }
}
