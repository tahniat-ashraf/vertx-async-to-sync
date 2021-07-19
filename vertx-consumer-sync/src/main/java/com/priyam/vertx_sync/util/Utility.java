package com.priyam.vertx_sync.util;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpResponse;

import java.util.UUID;

public class Utility {

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

  public static boolean isValidAcknowledgementResponse(HttpResponse<Buffer> ackResponse) {
    return ackResponse.bodyAsJsonObject().getString("status").equalsIgnoreCase("success");
  }

  public static String generateRequestId() {
    return "request-" + UUID.randomUUID();
  }

}
