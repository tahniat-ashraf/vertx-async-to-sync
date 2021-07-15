package com.priyam.vertx_async_core.eventbus;

import com.priyam.vertx_async_core.service.MongoClientService;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.client.WebClient;

public class PostServiceVerticle extends AbstractVerticle {

  private final static Logger LOG = LoggerFactory.getLogger("PostServiceVerticle");

  @Override
  public void start(Promise<Void> startFuture) throws Exception {

    var mongoClient = new MongoClientService();
    var webClient = WebClient.create(vertx);

    vertx
      .eventBus()
      .<JsonObject>consumer(EventBusAddress.FIND_ALL_POSTS.name())
      .handler(request -> {
        var ack = new JsonObject().put("status", "success");
        request.reply(ack);
        mongoClient.findAllPosts()
          .subscribe(posts -> webClient
            .post(9080, "localhost", "/callback")
            .rxSendJson(new JsonObject().put("posts", posts))
            .subscribe(bufferHttpResponse -> {
              System.out.println("bufferHttpResponse = " + bufferHttpResponse);
            }, Throwable::printStackTrace), Throwable::printStackTrace);

      });
  }
}
