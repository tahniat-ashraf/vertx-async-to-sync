package com.priyam.vertx_sync.handler;

import com.priyam.vertx_sync.model.CallbackMetadata;
import com.priyam.vertx_sync.util.Utility;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

public class AsyncCallbackHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger("AsyncCallbackHandler");
  private final Map<String, CallbackMetadata> callbackMap;


  public AsyncCallbackHandler() {
    this.callbackMap = new HashMap<>();
  }

  public void register(String requestId) {

    var timer = createTimeoutTimer(requestId);
    var callbackMetadata = createCallbackMetadata(timer);
    callbackMap.put(requestId, callbackMetadata);
    LOG.info("registered for callback :: requestId :: " + requestId);
  }

  public void unregister(String requestId) {
    if (callbackMap.containsKey(requestId)) {
      Vertx.currentContext().owner()
        .cancelTimer(callbackMap.get(requestId).getTimer());

      callbackMap.remove(requestId);//un-register requestId!

    }
  }

  private long createTimeoutTimer(String requestId) {
    return Vertx
      .currentContext()
      .owner()
      .setTimer(Utility.TIMEOUT_IN_MILLI, aLong -> {
        if (callbackMap.containsKey(requestId)) {
          callbackMap.remove(requestId);//un-register requestId!

          Vertx
            .currentContext()
            .owner()
            .eventBus()
            .send(requestId, Utility.DEFAULT_TIMEOUT_MESSAGE);
        }
      });
  }

  private CallbackMetadata createCallbackMetadata(long timer) {
    return CallbackMetadata
      .builder()
      .timer(timer)
      .build();
  }


  @Override
  public void handle(RoutingContext routingContext) {

    LOG.info("=> callback received :: requestId :: " + routingContext.getBodyAsJson().getString("requestId"));

    var callbackResponse = routingContext.getBodyAsJson();
    var requestId = callbackResponse.getString("requestId");

    if (callbackMap.containsKey(requestId)) {

      unregister(requestId);

      routingContext
        .vertx()
        .eventBus()
        .send(requestId, callbackResponse);

    } else {
      //if callback arrives in >29 second / duplicate callback, we ignore callback response
      LOG.error("requestId " + requestId + " isn't present in callbackMap");
      routingContext.request().response().setStatusCode(400);
    }

    routingContext.request().response().end();


  }
}
