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

        var requestBody = request.body();

        LOG.info("PostServiceVerticle :: requestBody " + requestBody);

        mongoClient.findAllPosts()
          .subscribe(posts -> {

            LOG.info("posts :: mongodb :: "+posts);

            webClient
              .post(requestBody.getInteger("port"), requestBody.getString("host"), requestBody.getString("uri"))
              .sendJson(new JsonObject().put("posts", posts).put("requestId", requestBody.getString("requestId")));
          }, throwable -> {
            LOG.error("Could not send response to callback url");
            throwable.printStackTrace();
          });

      });
  }
}
