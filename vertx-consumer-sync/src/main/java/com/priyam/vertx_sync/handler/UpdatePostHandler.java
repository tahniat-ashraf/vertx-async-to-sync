package com.priyam.vertx_sync.handler;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;

public class UpdatePostHandler implements Handler<RoutingContext> {

  private final AsyncCallbackHandler callbackHandler;

  public UpdatePostHandler(AsyncCallbackHandler callbackHandler) {
    this.callbackHandler = callbackHandler;
  }

  @Override
  public void handle(RoutingContext routingContext) {

  }
}
