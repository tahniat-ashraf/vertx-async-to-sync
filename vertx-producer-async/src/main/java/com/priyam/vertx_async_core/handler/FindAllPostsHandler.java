package com.priyam.vertx_async_core.handler;

import com.priyam.vertx_async_core.model.Post;
import com.priyam.vertx_async_core.service.MongoClientService;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.stream.Collectors;

public class FindAllPostsHandler implements Handler<RoutingContext> {

  private final MongoClientService mongoClientService;

  public FindAllPostsHandler() {
    this.mongoClientService = new MongoClientService();
  }

  @Override
  public void handle(RoutingContext routingContext) {


    mongoClientService
      .findAllPosts()
      .subscribe(postList -> {
        routingContext.response().putHeader("content-type", "application/json");

        var postsConvertedList = postList
          .stream()
          .map(entry -> entry.mapTo(Post.class))
          .map(JsonObject::mapFrom)
          .collect(Collectors.toList());

        routingContext.response().end(Json.encodePrettily(postsConvertedList));
      }, Throwable::printStackTrace);
  }
}
