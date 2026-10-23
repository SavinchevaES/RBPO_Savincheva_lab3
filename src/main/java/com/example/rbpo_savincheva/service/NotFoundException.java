package com.example.rbpo_savincheva.service;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String resource, long id) {
    super(resource + " with id=" + id + " was not found");
  }
}
