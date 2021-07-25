package com.priyam.vertx_async_core.exception;

import lombok.Getter;

public class MyCustomException extends RuntimeException {

  @Getter
  private final String errorMessage;

  public MyCustomException(String message) {
    super(message);
    this.errorMessage = message;
  }
}

