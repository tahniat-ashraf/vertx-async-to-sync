package com.priyam.vertx_sync;


import com.priyam.vertx_sync.handler.*;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.healthchecks.Status;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

public class ConsumerApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger("ConsumerApiVerticle");

  @Override
  public void start(Promise<Void> promise) throws Exception {


    var callbackHandler = new AsyncCallbackHandler();
    var findAllPostsHandler = new FindAllPostsHandler(callbackHandler);
    var createNewPostHandler = new CreateNewPostHandler(callbackHandler);
    var deletePostHandler = new DeletePostHandler(callbackHandler);
    var updatePostHandler = new UpdatePostHandler(callbackHandler);
    var getPostByIdHandler = new GetPostHandler(callbackHandler);

    HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);

    healthCheckHandler.register("health-check-proc", 2000, p -> {

      p.complete(Status.OK());

    });

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.get("/health").handler(healthCheckHandler);
    router.get("/posts").handler(findAllPostsHandler);
    router.get("/posts/:id").handler(getPostByIdHandler);
    router.delete("/posts/:id").handler(deletePostHandler);
    router.put("/posts").handler(updatePostHandler);
    router.post("/posts").handler(createNewPostHandler);
    router.post("/callback").handler(callbackHandler);


    vertx
      .createHttpServer()
      .requestHandler(router)
      .rxListen(9080)
      .subscribe(httpServer -> {
        LOG.info("ConsumerApiVerticle is up and running bro");
        promise.complete();
      }, throwable -> LOG.error("Failed to deploy ProducerApiVerticle", throwable));

  }

}

