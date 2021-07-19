package com.priyam.vertx_sync.handler;

import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;

import java.util.UUID;

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
      .put("port", 9080)
      .put("host","localhost")
      .put("uri","/callback");

    var messageConsumer = createMessageConsumer(routingContext, requestId);

    webClient
      .post(9081, "localhost", "/findAllPosts")
      .rxSendJson(request)
      .subscribe(ackResponse -> {
        LOG.info("ackResponse :: " + ackResponse.bodyAsJsonObject().encodePrettily());
        if (!isValidAcknowledgementResponse(ackResponse)) {
          messageConsumer.unregister();
          routingContext.response().putHeader("content-type", "application/json");
          routingContext.response().end(new JsonObject().put("status", "fail").encodePrettily());
        }
      });


  }


  private MessageConsumer<JsonObject> createMessageConsumer(RoutingContext routingContext, String requestId) {
    return routingContext
      .vertx()
      .eventBus()
      .<JsonObject>consumer(requestId)
      .handler(message -> {
        //callback response
        routingContext.response().putHeader("content-type", "application/json");
        routingContext.response().end(message.body().encodePrettily());
      });
  }

  private boolean isValidAcknowledgementResponse(HttpResponse<Buffer> ackResponse) {
    return ackResponse.bodyAsJsonObject().getString("status").equalsIgnoreCase("success");
  }
}
