package com.priyam.vertx_async_core.service;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.mongo.MongoClient;

import java.util.List;

public class MongoClientService {

  private final MongoClient mongoClient;
  private static final Logger LOG = LoggerFactory.getLogger("MongoClientService");

  public MongoClientService() {


    mongoClient = MongoClient.createShared(Vertx.currentContext().owner(),
      new JsonObject().put("db_name", "vertx-async-to-sync")
    );

  }


  public Maybe<String> createNewPost(JsonObject post) {

    LOG.info("createNewPost :: post :: " + post);

    return mongoClient
      .rxInsert("posts", post);

  }

  public Maybe<MongoClientUpdateResult> updatePost(JsonObject oldPost, JsonObject newPost) {

    LOG.info("updatePost :: oldPost :: " + oldPost + "; newPost :: " + newPost);

    return mongoClient
      .rxUpdateCollection("posts", oldPost, newPost);

  }

  public Single<List<JsonObject>> findPostById(JsonObject jsonObject) {

    return mongoClient
      .rxFind("posts", jsonObject);

  }

  public Single<List<JsonObject>> findAllPosts() {
    return mongoClient
      .rxFind("posts", new JsonObject());
  }

  public Maybe<MongoClientDeleteResult> deletePost(JsonObject jsonObject) {
    LOG.info("deletePost :: post :: " + jsonObject);

    return mongoClient
      .rxRemoveDocument("posts", jsonObject);

  }

}
