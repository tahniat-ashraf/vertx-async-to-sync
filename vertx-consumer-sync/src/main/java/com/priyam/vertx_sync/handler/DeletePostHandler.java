package com.priyam.vertx_sync.handler;

import com.priyam.vertx_sync.util.Utility;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.WebClient;

public class DeletePostHandler implements Handler<RoutingContext> {

  private final static Logger LOG = LoggerFactory.getLogger("DeletePostHandler");
  private final WebClient webClient;
  private final AsyncCallbackHandler callbackHandler;


  public DeletePostHandler(AsyncCallbackHandler callbackHandler) {
    webClient = WebClient.create(Vertx.currentContext().owner());
    this.callbackHandler = callbackHandler;
  }

  @Override
  public void handle(RoutingContext routingContext) {

    LOG.info("=> DeletePostHandler :: handle");

    var requestId = Utility.generateRequestId();
    var request = createDeletePostByIdRequest(requestId, Integer.parseInt(routingContext.pathParam("id")));

    callbackHandler.register(requestId);
    var messageConsumer = Utility.createMessageConsumer(routingContext, requestId);

    webClient
      .post(9081, "localhost", "/deletePost")
      .rxSendJson(request)
      .subscribe(ackResponse -> {
        if (!Utility.isValidAcknowledgementResponse(ackResponse)) {
          messageConsumer.unregister();
          routingContext.response().putHeader("content-type", "application/json");
          routingContext.response().end(ackResponse.bodyAsJsonObject().encodePrettily());
        }
      });


  }

  private JsonObject createDeletePostByIdRequest(String requestId, int postId) {
    return new JsonObject()
      .put("requestId", requestId)
      .put("postId", postId)
      .put("port", 9080)
      .put("host", "localhost")
      .put("uri", "/callback");
  }
}