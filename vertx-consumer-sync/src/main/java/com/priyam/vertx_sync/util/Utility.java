package com.priyam.vertx_sync.util;

import com.priyam.vertx_sync.handler.AsyncCallbackHandler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpResponse;

import java.util.UUID;

public class Utility {

  public final static JsonObject DEFAULT_TIMEOUT_MESSAGE = new JsonObject().put("status", "timeout");
  public final static long TIMEOUT_IN_MILLI = 29000;
  public final static String CALLBACK_HOST = "localhost";
  public final static String CALLBACK_URI = "/callback";
  public final static int CALLBACK_PORT = 9080;

  private Utility() {
  }

  public static MessageConsumer<JsonObject> createMessageConsumer(RoutingContext routingContext, String requestId) {
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

  public static void handleFailedAcknowledgementResponse(AsyncCallbackHandler callbackHandler, RoutingContext routingContext, String requestId, MessageConsumer<JsonObject> messageConsumer, HttpResponse<Buffer> ackResponse) {
    if (isInvalidAcknowledgementResponse(ackResponse)) {
      messageConsumer.unregister();
      callbackHandler.unregister(requestId);
      routingContext.response().putHeader("content-type", "application/json");
      routingContext.response().end(new JsonObject().put("status", "fail").encodePrettily());
    }
  }

  public static boolean isInvalidAcknowledgementResponse(HttpResponse<Buffer> ackResponse) {
    return !ackResponse.bodyAsJsonObject().getString("status").equalsIgnoreCase("success");
  }

  public static String generateRequestId() {
    return "request-" + UUID.randomUUID();
  }

}
