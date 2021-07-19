package com.priyam.vertx_sync.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CallbackMetadata {

  private final long timer;

}
