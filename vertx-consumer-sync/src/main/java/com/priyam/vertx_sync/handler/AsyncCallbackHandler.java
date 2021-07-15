package com.priyam.vertx_sync.handler;

import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.reactivex.ext.web.RoutingContext;

public class AsyncCallbackHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger("AsyncCallbackHandler");

  @Override
  public void handle(RoutingContext routingContext) {

    var jsonObject = routingContext.getBodyAsJson();
    var posts = jsonObject.getJsonArray("posts");

    System.out.println("posts = " + posts);

  }
}
