package com.priyam.vertx_sync.handler;

import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.WebClient;

public class FindAllPostsHandler implements Handler<RoutingContext> {

  private final static Logger LOG = LoggerFactory.getLogger("FindAllPostsHandler");
  private final WebClient webClient;


  public FindAllPostsHandler() {
    webClient = WebClient.create(Vertx.currentContext().owner());
  }

  @Override
  public void handle(RoutingContext routingContext) {

    var request = new JsonObject().put("callbackUrl","http://localhost:9080/posts");

    webClient
      .get(9081, "localhost","/findAllPosts")
      .rxSend()
//      .rxSendJson(request)
      .subscribe(bufferHttpResponse -> {
        routingContext.response().end(bufferHttpResponse.bodyAsJsonObject().encodePrettily());
      }, Throwable::printStackTrace);

  }
}
