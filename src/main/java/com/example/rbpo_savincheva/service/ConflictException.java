package com.example.rbpo_savincheva.service;

// Нарушено бизнес-правило: операция недопустима в текущем состоянии данных (HTTP 409)
public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }
}
