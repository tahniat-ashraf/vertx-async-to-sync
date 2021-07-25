package com.priyam.vertx_async_core.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

  private String status;
  private String message;
  private String requestId;
}

