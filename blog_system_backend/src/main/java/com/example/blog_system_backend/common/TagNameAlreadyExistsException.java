package com.example.blog_system_backend.common;

public class TagNameAlreadyExistsException extends RuntimeException {

  public TagNameAlreadyExistsException(String name) {
    super("Tag's name already exists: " + name);
  }
}
