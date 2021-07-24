package com.priyam.vertx_sync.handler;

import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public class FindAllPostsHandler extends AbstractRequestHandler implements Handler<RoutingContext> {

  private final static Logger LOG = LoggerFactory.getLogger("FindAllPostsHandler");

  public FindAllPostsHandler(AsyncCallbackHandler callbackHandler) {
    super("localhost", 9081, "/findAllPosts", callbackHandler);
  }

  @Override
  public void handle(RoutingContext routingContext) {

    LOG.info("=> FindAllPostsHandler :: handle");
    var requestId = generateRequestId();
    var request = createFindAllPostsRequest(requestId);

    handleRequest(routingContext, request, requestId);

  }


  private JsonObject createFindAllPostsRequest(String requestId) {
    return createCallBackJsonObject(requestId);
  }


}
