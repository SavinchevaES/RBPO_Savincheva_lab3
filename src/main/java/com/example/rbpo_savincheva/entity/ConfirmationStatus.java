package com.example.rbpo_savincheva.entity;

import java.util.Arrays;

public enum ConfirmationStatus {
  PENDING,
  CONFIRMED,
  REJECTED;

  public static ConfirmationStatus parse(String value) {
    for (ConfirmationStatus status : values()) {
      if (status.name().equalsIgnoreCase(value.trim())) {
        return status;
      }
    }
    throw new IllegalArgumentException(
        "Unknown confirmation status: " + value + ". Allowed: " + Arrays.toString(values()));
  }
}
