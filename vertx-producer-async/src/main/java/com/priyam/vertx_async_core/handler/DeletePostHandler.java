package com.priyam.vertx_async_core.handler;

import com.priyam.vertx_async_core.service.MongoClientService;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public class DeletePostHandler implements Handler<RoutingContext> {

  private final MongoClientService mongoClientService;
  private static final Logger LOG = LoggerFactory.getLogger("DeletePostHandler");

  public DeletePostHandler() {
    this.mongoClientService = new MongoClientService();
  }

  @Override
  public void handle(RoutingContext routingContext) {

    var id = routingContext.pathParam("id");
    LOG.info("delete :: request :: id :: " + id);

    mongoClientService
      .deletePost(new JsonObject().put("id",id))
      .subscribe(result -> {
        LOG.info("delete :: result :: " + result);
        routingContext.response().putHeader("content-type", "application/json");
        routingContext.response().end();
      }, Throwable::printStackTrace);
  }
}
