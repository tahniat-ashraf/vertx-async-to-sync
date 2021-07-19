package com.priyam.vertx_async_core.eventbus;

import com.priyam.vertx_async_core.model.Post;
import com.priyam.vertx_async_core.service.MongoClientService;
import com.priyam.vertx_async_core.util.Utility;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.ext.web.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

public class PostServiceVerticle extends AbstractVerticle {

  private final static Logger LOG = LoggerFactory.getLogger("PostServiceVerticle");

  @Override
  public void start(Promise<Void> startFuture) throws Exception {

    var mongoClient = new MongoClientService();
    var webClient = WebClient.create(vertx);

    createHandlers(mongoClient, webClient);
  }

  private void createHandlers(MongoClientService mongoClient, WebClient webClient) {
    createFindAllPostsHandler(mongoClient, webClient);
    createGetPostByIdHandler(mongoClient, webClient);
    createAddPostHandler(mongoClient, webClient);
  }

  private MessageConsumer<JsonObject> getMessageConsumer(EventBusAddress eventBusAddress) {
    return vertx
      .eventBus()
      .consumer(eventBusAddress.name());
  }

  private void createGetPostByIdHandler(MongoClientService mongoClient, WebClient webClient) {
    getMessageConsumer(EventBusAddress.GET_POST)
      .handler(request -> {
        request.reply(Utility.DEFAULT_ACK_RESPONSE);

        var requestBody = request.body();

        mongoClient.findPostById(new JsonObject().put("id", requestBody.getInteger("postId")))
          .subscribe(response -> webClient
            .post(requestBody.getInteger("port"), requestBody.getString("host"), requestBody.getString("uri"))
            .sendJson(createGetPostByIdCallback(requestBody, response)), throwable -> LOG.error("failed to send response to callback url", throwable));

      });
  }

  private void createAddPostHandler(MongoClientService mongoClient, WebClient webClient) {
    getMessageConsumer(EventBusAddress.ADD_POST)
      .handler(request -> {
        request.reply(Utility.DEFAULT_ACK_RESPONSE);

        var requestBody = request.body();
        var post = JsonObject.mapFrom(requestBody.mapTo(Post.class));

        mongoClient.createNewPost(post)
          .subscribe(response -> webClient
            .post(requestBody.getInteger("port"), requestBody.getString("host"), requestBody.getString("uri"))
            .sendJson(createAddPostCallback(requestBody, response)), throwable -> LOG.error("failed to send response to callback url", throwable));

      });
  }


  private void createFindAllPostsHandler(MongoClientService mongoClient, WebClient webClient) {
    getMessageConsumer(EventBusAddress.FIND_ALL_POSTS)
      .handler(request -> {
        request.reply(Utility.DEFAULT_ACK_RESPONSE);

        var requestBody = request.body();

        mongoClient.findAllPosts()
          .subscribe(posts -> webClient
            .post(requestBody.getInteger("port"), requestBody.getString("host"), requestBody.getString("uri"))
            .sendJson(createFindAllPostsCallback(requestBody, posts)), throwable -> LOG.error("failed to send response to callback url", throwable));

      });
  }

  private JsonObject createFindAllPostsCallback(JsonObject requestBody, List<JsonObject> posts) {
    return new JsonObject().put("posts", posts).put("requestId", requestBody.getString("requestId"));
  }

  private JsonObject createAddPostCallback(JsonObject requestBody, String post) {
    return new JsonObject().put("response", post).put("requestId", requestBody.getString("requestId"));
  }

  private JsonObject createGetPostByIdCallback(JsonObject requestBody, List<JsonObject> posts) {

    var postList = posts
      .stream()
      .map(postJsonObject -> postJsonObject.mapTo(Post.class))
      .collect(Collectors.toList());

    return new JsonObject().put("posts", postList).put("requestId", requestBody.getString("requestId"));
  }
}
