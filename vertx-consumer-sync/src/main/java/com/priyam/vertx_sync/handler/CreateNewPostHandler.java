package com.priyam.vertx_sync.handler;

import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public class CreateNewPostHandler extends AbstractRequestHandler implements Handler<RoutingContext> {

  private final static Logger LOG = LoggerFactory.getLogger("CreateNewPostHandler");

  public CreateNewPostHandler(AsyncCallbackHandler callbackHandler) {
    super("localhost", 9081, "/createNewPost", callbackHandler);
  }

  @Override
  public void handle(RoutingContext routingContext) {

    LOG.info("=> CreateNewPostHandler :: handle");

    var requestId = generateRequestId();
    var request = createAddNewPostRequest(requestId, routingContext.getBodyAsJson());

    handleRequest(routingContext, request, requestId);

  }

  private JsonObject createAddNewPostRequest(String requestId, JsonObject jsonObject) {
    return createCallBackJsonObject(requestId)
      .put("post", jsonObject);

  }


}
