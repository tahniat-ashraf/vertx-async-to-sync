package com.priyam.vertx_sync.handler;

import com.priyam.vertx_sync.util.Utility;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public class DeletePostHandler  extends AbstractRequestHandler implements Handler<RoutingContext> {

  private final static Logger LOG = LoggerFactory.getLogger("DeletePostHandler");

  public DeletePostHandler(AsyncCallbackHandler callbackHandler) {
    super("localhost", 9081, "/deletePost", callbackHandler);

  }

  @Override
  public void handle(RoutingContext routingContext) {

    LOG.info("=> DeletePostHandler :: handle");

    var requestId = Utility.generateRequestId();
    var request = createDeletePostByIdRequest(requestId, Integer.parseInt(routingContext.pathParam("id")));

    handleRequest(routingContext, request, requestId);

  }

  private JsonObject createDeletePostByIdRequest(String requestId, int postId) {
    return createCallBackJsonObject(requestId)
      .put("postId", postId);
  }
}
