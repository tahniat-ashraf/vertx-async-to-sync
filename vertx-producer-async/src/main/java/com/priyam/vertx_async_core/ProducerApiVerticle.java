package com.priyam.vertx_async_core;

import com.priyam.vertx_async_core.handler.*;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.healthchecks.Status;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

public class ProducerApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger("ProducerApiVerticle");

  @Override
  public void start(Promise<Void> promise) throws Exception {


    var findAllPostsHandler = new FindAllPostsHandler();
    var createPostHandler = new CreatePostHandler();
    var updatePostHandler = new UpdatePostHandler();
    var deletePostHandler = new DeletePostHandler();
    var findPostByIdHandler = new FindPostByIdHandler();

    HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);

    healthCheckHandler.register("health-check-proc", 2000, p -> {

      p.complete(Status.OK());

    });

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.get("/health").handler(healthCheckHandler);
    router.get("/findAllPosts").handler(findAllPostsHandler);
    router.get("/posts/:id").handler(findPostByIdHandler);
    router.post("/posts").handler(createPostHandler);
    router.delete("/posts/:id").handler(deletePostHandler);
    router.put("/posts").handler(updatePostHandler);


    vertx
      .createHttpServer()
      .requestHandler(router)
      .rxListen(9081)
      .subscribe(httpServer -> {
        LOG.info("ProducerApiVerticle is up and running bro");
        promise.complete();
      }, throwable -> LOG.error("Failed to deploy ProducerApiVerticle", throwable));

  }
}
