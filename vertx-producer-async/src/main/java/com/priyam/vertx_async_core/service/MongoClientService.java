package com.priyam.vertx_async_core.service;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.*;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.mongo.MongoClient;

import java.util.ArrayList;
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

  public Maybe<MongoClientBulkWriteResult> bulkAddPosts() {

    final List<BulkOperation> bulkOperationList = new ArrayList<>();

    return Vertx.currentContext()
      .owner()
      .fileSystem()
      .rxReadFile("fake-posts.json")
      .map(Buffer::toJsonArray)
      .map(JsonArray::getList)
      .toFlowable()
      .flatMap(list -> Flowable.fromIterable(list))
      .map(obj -> {
        var document = JsonObject.mapFrom(obj);
        bulkOperationList.add(BulkOperation.createInsert(document));
        return document;
      })
      .toList()
      .toMaybe()
      .flatMap(list -> mongoClient.rxBulkWrite("posts", bulkOperationList));


  }

  public Maybe<MongoClientUpdateResult> updatePost(JsonObject query, JsonObject newPost) {

    LOG.info("updatePost :: query :: " + query + "; newPost :: " + newPost);

    UpdateOptions options = new UpdateOptions().setMulti(false).setUpsert(false);

    return mongoClient
      .rxUpdateCollectionWithOptions("posts", query, newPost, options);

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
      .rxRemoveDocument("posts", jsonObject)
      .doOnError(throwable -> {
        LOG.error("deletePost :: error ", throwable);
      });

  }

}
