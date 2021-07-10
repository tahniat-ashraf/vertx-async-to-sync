package com.priyam.vertx_async_core.handler;

import com.priyam.vertx_async_core.service.MongoClientService;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.reactivex.ext.web.RoutingContext;

public class CreatePostHandler implements Handler<RoutingContext> {

  private final MongoClientService mongoClientService;
  private static final Logger LOG = LoggerFactory.getLogger("CreatePostHandler");


  public CreatePostHandler() {
    this.mongoClientService = new MongoClientService();
  }

  @Override
  public void handle(RoutingContext routingContext) {

    var request = routingContext.getBodyAsJson();
    LOG.info("create :: request (jsonObject) :: "+request.encode());

    mongoClientService
      .createNewPost(request)
      .subscribe(result -> {
        LOG.info("create :: result :: " + result);
        routingContext.response().putHeader("content-type", "application/json");
        routingContext.response().end(request.encodePrettily());
      }, Throwable::printStackTrace);

  }
}
