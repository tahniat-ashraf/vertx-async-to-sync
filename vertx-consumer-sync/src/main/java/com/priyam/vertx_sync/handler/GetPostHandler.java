package com.priyam.vertx_sync.handler;

import com.priyam.vertx_sync.util.Utility;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public class GetPostHandler extends AbstractRequestHandler implements Handler<RoutingContext> {

  private final static Logger LOG = LoggerFactory.getLogger("GetPostHandler");


  public GetPostHandler(AsyncCallbackHandler callbackHandler) {
    super("localhost", 9081, "/getPost", callbackHandler);

  }

  @Override
  public void handle(RoutingContext routingContext) {

    LOG.info("=> GetPostHandler :: handle");

    var requestId = Utility.generateRequestId();
    var request = createGetPostByIdRequest(requestId, Integer.parseInt(routingContext.pathParam("id")));

    handleRequest(routingContext, request, requestId);

  }

  private JsonObject createGetPostByIdRequest(String requestId, int postId) {
    return createCallBackJsonObject(requestId)
      .put("postId", postId);

  }

}
