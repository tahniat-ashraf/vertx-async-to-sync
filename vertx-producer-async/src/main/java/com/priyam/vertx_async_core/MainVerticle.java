package com.priyam.vertx_async_core;


import com.priyam.vertx_async_core.eventbus.PostServiceVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger("MainVerticle");

  @Override
  public void start(Promise<Void> promise) throws Exception {
    vertx
      .rxDeployVerticle(ProducerApiVerticle.class.getName())
      .flatMap(s -> vertx.rxDeployVerticle(PostServiceVerticle.class.getName()))
      .subscribe((s) -> {
        LOG.info("All verticles are up & running");
        promise.complete();
      }, promise::fail);
  }
}
