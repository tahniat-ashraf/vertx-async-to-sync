package com.priyam.vertx_sync.handler;

import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.WebClient;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FindAllPostsHandler implements Handler<RoutingContext> {

  private final static Logger LOG = LoggerFactory.getLogger("FindAllPostsHandler");
  private final WebClient webClient;
  private final AsyncCallbackHandler callbackHandler;


  public FindAllPostsHandler(AsyncCallbackHandler callbackHandler) {
    webClient = WebClient.create(Vertx.currentContext().owner());
    this.callbackHandler = callbackHandler;
  }

  @Override
  public void handle(RoutingContext routingContext) {

    LOG.info("=> FindAllPostsHandler :: handle");

    var requestId = "request-" + UUID.randomUUID();
    var request = new JsonObject()
      .put("requestId", requestId)
      .put("callbackUrl", "http://localhost:9080/posts");

    MessageConsumer<JsonObject> register = callbackHandler
      .register(requestId);

    webClient
      .post(9081, "localhost", "/findAllPosts")
      .rxSendJson(request)
      .filter(ackResponse -> ackResponse.bodyAsJsonObject().getString("status").equalsIgnoreCase("success"))
      .delay(1, TimeUnit.SECONDS)//1 second imposed delay for handler registration complete. Need to improve!
      .flatMap(ackResponse -> {


        LOG.info("ackResponse :: " + ackResponse.bodyAsJsonObject().encodePrettily());

        return routingContext
          .vertx()
          .eventBus()
          .<JsonObject>rxRequest(requestId, request).toMaybe();


      })
      .subscribe(message -> {
        //callback response
        routingContext.response().putHeader("content-type", "application/json");
        routingContext.response().end(message.body().encodePrettily());

      }, Throwable::printStackTrace);


  }
}
