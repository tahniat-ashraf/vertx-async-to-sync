package com.priyam.vertx_sync.handler;

import com.priyam.vertx_sync.util.Utility;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;

import java.util.UUID;

public abstract class AbstractRequestHandler {

  private final String host;
  private final int port;
  private final String uri;
  private final AsyncCallbackHandler callbackHandler;
  private final WebClient webClient;

  public AbstractRequestHandler(String host, int port, String uri, AsyncCallbackHandler callbackHandler) {
    this.host = host;
    this.port = port;
    this.uri = uri;
    this.callbackHandler = callbackHandler;
    webClient = WebClient.create(Vertx.currentContext().owner());
  }

  protected void handleRequest(RoutingContext routingContext, JsonObject request, String requestId) {


    registerRequestIdForCallback(requestId);
    var messageConsumer = createCallbackResponseConsumer(routingContext, requestId);

    webClient
      .post(port, host, uri)
      .rxSendJson(request)
      .subscribe(ackResponse -> handleFailedAcknowledgementResponse(routingContext, messageConsumer, ackResponse, requestId));
  }

  protected JsonObject createCallBackJsonObject(String requestId) {
    return new JsonObject()
      .put("requestId", requestId)
      .put("port", Utility.CALLBACK_PORT)
      .put("host", Utility.CALLBACK_HOST)
      .put("uri", Utility.CALLBACK_URI);
  }

  protected String generateRequestId() {
    return "request-" + UUID.randomUUID();
  }

  private MessageConsumer<JsonObject> createCallbackResponseConsumer(RoutingContext routingContext, String requestId) {
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

  private void registerRequestIdForCallback(String requestId) {
    callbackHandler.register(requestId);
  }

  private void handleFailedAcknowledgementResponse(RoutingContext routingContext, MessageConsumer<JsonObject> messageConsumer, HttpResponse<Buffer> ackResponse, String requestId) {
    if (isInvalidAcknowledgementResponse(ackResponse)) {
      messageConsumer.unregister();
      callbackHandler.unregister(requestId);
      routingContext.response().putHeader("content-type", "application/json");
      routingContext.response().end(new JsonObject().put("status", "fail").encodePrettily());
    }
  }

  private boolean isInvalidAcknowledgementResponse(HttpResponse<Buffer> ackResponse) {
    return !ackResponse.bodyAsJsonObject().getString("status").equalsIgnoreCase("success");
  }

}
