package com.priyam.vertx_async_core.handler;

import com.priyam.vertx_async_core.service.MongoClientService;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public class UpdatePostHandler implements Handler<RoutingContext> {

  private final MongoClientService mongoClientService;
  private static final Logger LOG = LoggerFactory.getLogger("DeletePostHandler");

  public UpdatePostHandler() {
    this.mongoClientService = new MongoClientService();
  }

  @Override
  public void handle(RoutingContext routingContext) {

    var request = routingContext.getBodyAsJson();
    LOG.info("update :: request (jsonObject) :: " + request.encode());

    var newJsonObject = new JsonObject()
      .put("title", "changed title")
      .put("body", "changed body");


    mongoClientService
      .updatePost(request, newJsonObject)
      .subscribe(result -> {
        LOG.info("update :: result :: " + result);
        routingContext.response().putHeader("content-type", "application/json");
        routingContext.response().end(request.encodePrettily());
      }, Throwable::printStackTrace);
  }
}
