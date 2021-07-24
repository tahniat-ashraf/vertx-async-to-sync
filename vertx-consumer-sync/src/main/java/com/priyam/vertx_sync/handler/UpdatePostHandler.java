package com.priyam.vertx_sync.handler;

import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public class UpdatePostHandler extends AbstractRequestHandler implements Handler<RoutingContext> {


  private final static Logger LOG = LoggerFactory.getLogger("DeletePostHandler");

  public UpdatePostHandler(AsyncCallbackHandler callbackHandler) {
    super("localhost", 9081, "/updatePost", callbackHandler);

  }

  @Override
  public void handle(RoutingContext routingContext) {

    LOG.info("=> UpdatePostHandler :: handle");
    var requestId = generateRequestId();
    var request = createUpdatePostRequest(requestId, routingContext.getBodyAsJson());

    handleRequest(routingContext, request, requestId);

  }

  private JsonObject createUpdatePostRequest(String requestId, JsonObject request) {
    return createCallBackJsonObject(requestId)
      .put("post", request);
  }
}
