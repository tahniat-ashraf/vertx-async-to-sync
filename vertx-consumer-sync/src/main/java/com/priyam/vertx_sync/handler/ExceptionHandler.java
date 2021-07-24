package com.priyam.vertx_sync.handler;

import com.priyam.vertx_sync.model.MyCustomException;
import com.priyam.vertx_sync.model.response.ErrorResponse;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.ext.web.validation.BadRequestException;
import io.vertx.reactivex.ext.web.RoutingContext;

public class ExceptionHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger("ExceptionHandler");

  @Override
  public void handle(RoutingContext routingContext) {
    Throwable err = routingContext.failure();
    LOG.error("handle :: err", err);
    routingContext.response().putHeader("content-type", "application/json");
    var errResp = ErrorResponse.builder().build();

    if (err instanceof BadRequestException) {
      var badReqEx = (BadRequestException) err;
      errResp.setErrorMessage(badReqEx.toJson().toString());
      routingContext.response().setStatusCode(400).end(Json.encode(errResp));
    } else if (err instanceof IllegalArgumentException) {
      routingContext.response().setStatusCode(400).end();
    } else if (err instanceof MyCustomException) {
      var myCustomException = (MyCustomException) err;
      errResp.setErrorMessage(myCustomException.getErrorMessage());
      routingContext.response().setStatusCode(400).end(Json.encode(errResp));
    } else {
      errResp.setErrorMessage("An error has occurred");
      routingContext.response().setStatusCode(500).end(Json.encode(errResp));
    }
  }
}
