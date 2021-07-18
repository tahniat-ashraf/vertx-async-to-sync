package com.priyam.vertx_sync.handler;

import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

public class AsyncCallbackHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger("AsyncCallbackHandler");
  private final Map<String, MessageConsumer<JsonObject>> consumerMap;


  public AsyncCallbackHandler() {
    consumerMap = new HashMap<>();
  }

  public MessageConsumer<JsonObject> register(String requestId) {

    LOG.info("=> register");

    MessageConsumer<JsonObject> consumer = Vertx
      .currentContext()
      .owner()
      .eventBus()
      .consumer(requestId);

    consumerMap.put(requestId, consumer);

    return consumer;


  }


  @Override
  public void handle(RoutingContext routingContext) {

    LOG.info("=> **** callback has arrived ***");

    var callbackResponse = routingContext.getBodyAsJson();
    var requestId = callbackResponse.getString("requestId");

    LOG.info("callbackResponse " + callbackResponse);

    LOG.info("consumerMap (1) :: " + consumerMap);

    MessageConsumer<JsonObject> handler = consumerMap
      .get(requestId)
      .handler(message -> message.reply(callbackResponse));

    consumerMap.put(requestId, handler);

    LOG.info("consumerMap (2) :: " + consumerMap);

  }
}
