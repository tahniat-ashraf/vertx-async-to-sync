package com.priyam.vertx_async_core.exception;

import lombok.Builder;
import lombok.Getter;


@Getter
public class MyCustomCallbackException extends RuntimeException {

  private final String errorMessage;
  private final String host;
  private final int port;
  private final String uri;
  private final String requestId;

  @Builder
  public MyCustomCallbackException(String message, String host, int port, String uri, String requestId) {
    super(message);
    this.errorMessage = message;
    this.host = host;
    this.port = port;
    this.uri = uri;
    this.requestId = requestId;
  }
}
