package com.priyam.vertx_sync.handler;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;

public class DeletePostHandler implements Handler<RoutingContext> {

  private final AsyncCallbackHandler callbackHandler;

  public DeletePostHandler(AsyncCallbackHandler callbackHandler) {
    this.callbackHandler = callbackHandler;
  }

  @Override
  public void handle(RoutingContext routingContext) {

  }
}
