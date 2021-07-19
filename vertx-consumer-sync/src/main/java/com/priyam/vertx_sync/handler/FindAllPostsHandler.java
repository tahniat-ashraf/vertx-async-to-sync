package com.priyam.vertx_sync.handler;

import com.priyam.vertx_sync.util.Utility;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.WebClient;

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

    var requestId = Utility.generateRequestId();
    var request = createFindAllPostsRequest(requestId);

    callbackHandler.register(requestId);
    var messageConsumer = Utility.createMessageConsumer(routingContext, requestId);

    webClient
      .post(9081, "localhost", "/findAllPosts")
      .rxSendJson(request)
      .subscribe(ackResponse -> {
        if (!Utility.isValidAcknowledgementResponse(ackResponse)) {
          messageConsumer.unregister();
          routingContext.response().putHeader("content-type", "application/json");
          routingContext.response().end(ackResponse.bodyAsJsonObject().encodePrettily());
        }
      });


  }

  private JsonObject createFindAllPostsRequest(String requestId) {
    return new JsonObject()
      .put("requestId", requestId)
      .put("port", 9080)
      .put("host", "localhost")
      .put("uri", "/callback");
  }


}
