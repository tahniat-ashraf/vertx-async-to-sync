package com.priyam.vertx_async_core.handler;

import com.priyam.vertx_async_core.eventbus.EventBusAddress;
import com.priyam.vertx_async_core.util.Utility;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public class CreatePostHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger("CreatePostHandler");

  @Override
  public void handle(RoutingContext routingContext) {


    var request = routingContext.getBodyAsJson();

    LOG.info("handle :: request :: " + request.encode());

    routingContext.response().putHeader("content-type", "application/json");

    routingContext
      .vertx()
      .eventBus()
      .<JsonObject>rxRequest(EventBusAddress.ADD_POST.name(), request)
      .subscribe(message -> routingContext.response().end(message.body().encode()),
        throwable -> {
          routingContext.response().end(Utility.DEFAULT_FAIL_MESSAGE.encode());
        });

  }
}
