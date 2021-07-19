package com.priyam.vertx_async_core.handler;

import com.priyam.vertx_async_core.eventbus.EventBusAddress;
import com.priyam.vertx_async_core.util.Utility;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public class FindAllPostsHandler implements Handler<RoutingContext> {

  private final Logger LOG = LoggerFactory.getLogger("FindAllPostsHandler");

  @Override
  public void handle(RoutingContext routingContext) {

    var request = routingContext.getBodyAsJson();

    LOG.info("handle :: request :: " + request);

    routingContext.response().putHeader("content-type", "application/json");

    routingContext
      .vertx()
      .eventBus()
      .<JsonObject>rxRequest(EventBusAddress.FIND_ALL_POSTS.name(), request)
      .subscribe(message -> routingContext.response().end(message.body().encode()),
        throwable -> {
          LOG.error("error while doing FIND_ALL_POSTS :: ", throwable);
          routingContext.response().end(Utility.DEFAULT_FAIL_MESSAGE.encode());
        });

  }
}
