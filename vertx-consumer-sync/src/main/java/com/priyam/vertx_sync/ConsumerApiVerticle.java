package com.priyam.vertx_sync;


import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.healthchecks.Status;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;
import io.vertx.reactivex.ext.web.Router;

public class ConsumerApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger("ConsumerApiVerticle");

  @Override
  public void start(Promise<Void> promise) throws Exception {

    HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);

    healthCheckHandler.register("health-check-proc", 2000, p -> {

      //Checking timeout
//      vertx
//        .setTimer(3000, aLong -> {
//          p.complete(Status.OK());
//        });
      p.complete(Status.OK());
//      p.complete(Status.KO());

    });

    Router router = Router.router(vertx);

    router.get("/health").handler(healthCheckHandler);


    vertx
      .createHttpServer()
      .requestHandler(router)
      .rxListen(9080)
      .subscribe(httpServer -> {
        LOG.info("ConsumerApiVerticle is up and running bro");
        promise.complete();
      }, throwable -> {
        LOG.error("Failed to deploy ConsumerApiVerticle");
      });

  }
}
