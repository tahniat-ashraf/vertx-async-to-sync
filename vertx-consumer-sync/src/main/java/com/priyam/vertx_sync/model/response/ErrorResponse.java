package com.priyam.vertx_sync.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

  private String errorMessage;
}
