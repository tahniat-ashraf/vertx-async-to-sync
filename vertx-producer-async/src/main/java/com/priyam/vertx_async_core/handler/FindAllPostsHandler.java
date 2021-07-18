package com.priyam.vertx_async_core.handler;

import com.priyam.vertx_async_core.eventbus.EventBusAddress;
import com.priyam.vertx_async_core.service.MongoClientService;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public class FindAllPostsHandler implements Handler<RoutingContext> {

  private final MongoClientService mongoClientService;
  private final Logger LOG = LoggerFactory.getLogger("FindAllPostsHandler");

  public FindAllPostsHandler() {
    this.mongoClientService = new MongoClientService();
  }

  @Override
  public void handle(RoutingContext routingContext) {

    var request = routingContext.getBodyAsJson();

    LOG.info("FindAllPostsHandler :: request :: "+request);

    routingContext.response().putHeader("content-type", "application/json");

    routingContext
      .vertx()
      .eventBus()
      .<JsonObject>rxRequest(EventBusAddress.FIND_ALL_POSTS.name(), request)
      .subscribe(message -> routingContext.response().end(message.body().encode()),
        throwable -> {
          LOG.error("Problem while processing FIND_ALL_POSTS request", throwable);
          var response = new JsonObject().put("status", "fail");
          routingContext.response().end(response.encode());
        });

  }
}
