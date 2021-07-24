package com.priyam.vertx_async_core.handler;

import com.priyam.vertx_async_core.exception.MyCustomCallbackException;
import com.priyam.vertx_async_core.model.response.ErrorResponse;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.validation.BadRequestException;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

public class ExceptionHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger("ExceptionHandler");

  @Override
  public void handle(RoutingContext routingContext) {
    Throwable err = routingContext.failure();
    LOG.error("handle :: err", err);
    routingContext.response().putHeader("content-type", "application/json");
    var errResp = ErrorResponse.builder().status("fail").build();

    if (err instanceof BadRequestException) {
      var badReqEx = (BadRequestException) err;
      errResp.setMessage(badReqEx.toJson().toString());
      routingContext.response().setStatusCode(400).end(Json.encode(errResp));
    } else if (err instanceof IllegalArgumentException) {
      routingContext.response().setStatusCode(400).end();
    } else if (err instanceof MyCustomCallbackException) {
      var myCustomException = (MyCustomCallbackException) err;
      errResp.setMessage(myCustomException.getErrorMessage());
      errResp.setRequestId(myCustomException.getRequestId());
      var webclient = WebClient.create(routingContext.vertx());
      webclient
        .post(myCustomException.getPort(), myCustomException.getHost(), myCustomException.getUri())
        .as(BodyCodec.none())
        .sendJsonObject(JsonObject.mapFrom(errResp));
    } else {
      errResp.setMessage("An error has occurred");
      routingContext.response().setStatusCode(500).end(Json.encode(errResp));
    }
  }
}
