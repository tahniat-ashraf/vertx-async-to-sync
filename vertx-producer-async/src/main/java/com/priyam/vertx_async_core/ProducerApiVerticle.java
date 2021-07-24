package com.priyam.vertx_async_core;

import com.priyam.vertx_async_core.eventbus.EventBusAddress;
import com.priyam.vertx_async_core.handler.ExceptionHandler;
import com.priyam.vertx_async_core.util.Utility;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.Status;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

public class ProducerApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger("ProducerApiVerticle");

  @Override
  public void start(Promise<Void> promise) throws Exception {

    HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);
    ExceptionHandler exceptionHandler = new ExceptionHandler();

    healthCheckHandler.register("health-check-proc", 2000, p -> {

      p.complete(Status.OK());

    });

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.get("/health").handler(healthCheckHandler);
    router.post("/findAllPosts").handler(routingContext -> requestHandler(routingContext, EventBusAddress.FIND_ALL_POSTS)).failureHandler(exceptionHandler);
    router.post("/getPost").handler(routingContext -> requestHandler(routingContext, EventBusAddress.GET_POST)).failureHandler(exceptionHandler);
    router.post("/createNewPost").handler(routingContext -> requestHandler(routingContext, EventBusAddress.ADD_POST)).failureHandler(exceptionHandler);
    router.post("/deletePost").handler(routingContext -> requestHandler(routingContext, EventBusAddress.DELETE_POST)).failureHandler(exceptionHandler);
    router.post("/updatePost").handler(routingContext -> requestHandler(routingContext, EventBusAddress.UPDATE_POST)).failureHandler(exceptionHandler);


    vertx
      .createHttpServer()
      .requestHandler(router)
      .rxListen(9081)
      .subscribe(httpServer -> {
        LOG.info("ProducerApiVerticle is up and running bro");
        promise.complete();
      }, throwable -> LOG.error("Failed to deploy ProducerApiVerticle", throwable));

  }

  private void requestHandler(RoutingContext routingContext, EventBusAddress eventBusAddress) {
    var request = getRequestBody(routingContext);
    LOG.info("requestHandler :: request :: " + request.encode() + "; eventBusAddress :: " + eventBusAddress.name());

    routingContext.response().putHeader("content-type", "application/json");

    routingContext
      .vertx()
      .eventBus()
      .<JsonObject>rxRequest(eventBusAddress.name(), request)
      .subscribe(message ->
        {
          var ackResponse = message.body().encodePrettily();
          LOG.info("requestHandler :: ackResponse :: " + ackResponse);
          routingContext.response().end(ackResponse);
        },
        throwable -> {
          LOG.error("requestHandler :: error :: ", throwable);
          routingContext.response().end(Utility.DEFAULT_FAIL_MESSAGE.encode());
        });
  }

  private JsonObject getRequestBody(RoutingContext routingContext) {
    return routingContext.getBodyAsJson();
  }


}
