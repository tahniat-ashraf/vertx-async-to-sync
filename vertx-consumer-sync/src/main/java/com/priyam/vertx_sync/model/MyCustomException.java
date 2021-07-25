package com.priyam.vertx_sync.model;

import com.priyam.vertx_sync.model.response.ErrorResponse;
import lombok.Getter;


public class MyCustomException extends RuntimeException {

  @Getter
  private final String errorMessage;

  public MyCustomException(ErrorResponse errorResponse) {
    super(errorResponse.getErrorMessage());
    this.errorMessage = errorResponse.getErrorMessage();
  }
}
