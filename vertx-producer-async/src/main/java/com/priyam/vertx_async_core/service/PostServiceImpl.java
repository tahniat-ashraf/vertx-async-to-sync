package com.priyam.vertx_async_core.service;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

  private final MongoClientService mongoClientService;

  @Override
  public Single<List<JsonObject>> findAllPosts() {
    return mongoClientService
      .findAllPosts();
  }

}
