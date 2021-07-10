package com.priyam.vertx_sync;


import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger("MainVerticle");


  @Override
  public void start(Promise<Void> promise) throws Exception {


    vertx
      .rxDeployVerticle(ConsumerApiVerticle.class.getName())
      .subscribe((s) -> {
        LOG.info("All verticles are up and running brotha");
        promise.complete();
      }, promise::fail);

  }

}
