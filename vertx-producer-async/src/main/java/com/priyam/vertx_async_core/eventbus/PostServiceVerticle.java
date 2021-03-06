package com.priyam.vertx_async_core.eventbus;

import com.priyam.vertx_async_core.exception.MyCustomCallbackException;
import com.priyam.vertx_async_core.model.Post;
import com.priyam.vertx_async_core.model.response.ErrorResponse;
import com.priyam.vertx_async_core.service.MongoClientService;
import com.priyam.vertx_async_core.util.Utility;
import io.reactivex.Maybe;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

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
    createDeletePostByIdHandler(mongoClient, webClient);
    createUpdatePostHandler(mongoClient, webClient);
    createBulkAddPostHandler(mongoClient);
  }

  private MessageConsumer<JsonObject> getMessageConsumer(EventBusAddress eventBusAddress) {
    return vertx
      .eventBus()
      .consumer(eventBusAddress.name());
  }

  private void createGetPostByIdHandler(MongoClientService mongoClient, WebClient webClient) {

    getMessageConsumer(EventBusAddress.GET_POST)
      .handler(request -> {
        var requestBody = request.body();
        sendAcknowledgementResponse(request, getRequestId(requestBody));

        mongoClient.findPostById(new JsonObject().put("id", requestBody.getInteger("postId")))
          .subscribe(response -> webClient
              .post(getPort(requestBody), getHost(requestBody), getUri(requestBody))
              .as(BodyCodec.none())
              .sendJsonObject(createGetPostByIdCallback(requestBody, response)),
            throwable -> {
              LOG.error("failed to send response to callback url", throwable);
              handleCustomCallbackException(webClient, throwable);
            });

      });
  }


  private void createDeletePostByIdHandler(MongoClientService mongoClient, WebClient webClient) {

    getMessageConsumer(EventBusAddress.DELETE_POST)
      .handler(request -> {
        var requestBody = request.body();
        sendAcknowledgementResponse(request, getRequestId(requestBody));

        mongoClient.deletePost(new JsonObject().put("id", requestBody.getInteger("postId")))
          .subscribe(response -> webClient
              .post(getPort(requestBody), getHost(requestBody), getUri(requestBody))
              .as(BodyCodec.none())
              .sendJsonObject(createDeletePostByIdCallback(requestBody, response)),
            throwable -> {
              LOG.error("error :: ", throwable);
              handleCustomCallbackException(webClient, throwable);
            });

      });
  }

  private void createBulkAddPostHandler(MongoClientService mongoClient) {

    getMessageConsumer(EventBusAddress.BULK_ADD_POSTS)
      .handler(request -> mongoClient.bulkAddPosts()
        .subscribe(mongoClientBulkWriteResult -> {
            request.reply(mongoClientBulkWriteResult.toJson().put("status", "success"));
          },
          throwable -> {
            LOG.error("createBulkAddPostHandler :: error :: ", throwable);
          }));
  }


  private void createAddPostHandler(MongoClientService mongoClient, WebClient webClient) {

    getMessageConsumer(EventBusAddress.ADD_POST)
      .handler(request -> {
        var requestBody = request.body();
        var requestId = getRequestId(requestBody);
        var post = JsonObject.mapFrom(requestBody.getJsonObject("post").mapTo(Post.class));
        var host = getHost(requestBody);
        var port = getPort(requestBody);
        var uri = getUri(requestBody);

        sendAcknowledgementResponse(request, requestId);

        mongoClient.findPostById(new JsonObject().put("id", post.getInteger("id")))
          .filter(posts -> posts.size() == 0)
          .switchIfEmpty(Maybe.error(
            MyCustomCallbackException.builder().host(host).port(port).uri(uri)
              .requestId(requestId)
              .message("Post already exists")
              .build()))
          .flatMap(posts -> mongoClient.createNewPost(post))
          .subscribe(response -> webClient
              .post(port, host, uri)
              .as(BodyCodec.none())
              .sendJsonObject(createAddPostCallback(requestBody, response)),
            throwable -> {
              LOG.error("error :: ", throwable);
              handleCustomCallbackException(webClient, throwable);

            });

      });
  }

  private void createUpdatePostHandler(MongoClientService mongoClient, WebClient webClient) {

    getMessageConsumer(EventBusAddress.UPDATE_POST)
      .handler(request -> {
        var requestBody = request.body();
        var requestId = getRequestId(requestBody);
        var post = JsonObject.mapFrom(requestBody.getJsonObject("post").mapTo(Post.class));
        var host = getHost(requestBody);
        var port = getPort(requestBody);
        var uri = getUri(requestBody);

        mongoClient.findPostById(new JsonObject().put("id", post.getInteger("id")))
          .filter(posts -> posts.size() == 1)
          .switchIfEmpty(Maybe.error(
            MyCustomCallbackException.builder().host(host).port(port).uri(uri)
              .requestId(requestId)
              .message("Post doesn't exist")
              .build()))
          .flatMap(posts -> mongoClient.updatePost(new JsonObject().put("id", posts.get(0).getInteger("id")),
            new JsonObject().put("$set", post)))
          .subscribe(response -> webClient
              .post(port, host, uri)
              .as(BodyCodec.none())
              .sendJsonObject(createUpdatePostCallback(requestBody, response)),
            throwable -> {
              LOG.error("failed to send response to callback url", throwable);
              handleCustomCallbackException(webClient, throwable);
            });

      });
  }


  private void createFindAllPostsHandler(MongoClientService mongoClient, WebClient webClient) {

    getMessageConsumer(EventBusAddress.FIND_ALL_POSTS)
      .handler(request -> {
        var requestBody = request.body();
        sendAcknowledgementResponse(request, getRequestId(requestBody));

        mongoClient.findAllPosts()
          .subscribe(posts -> webClient
              .post(getPort(requestBody), getHost(requestBody), getUri(requestBody))
              .as(BodyCodec.none())
              .sendJsonObject(createFindAllPostsCallback(requestBody, posts)),
            throwable -> {
              LOG.error("failed to send response to callback url", throwable);
              handleCustomCallbackException(webClient, throwable);
            });

      });
  }

  private void handleCustomCallbackException(WebClient webClient, Throwable throwable) {
    if (throwable instanceof MyCustomCallbackException) {
      var errResp = ErrorResponse.builder().status("fail").build();
      var myCustomException = (MyCustomCallbackException) throwable;
      errResp.setMessage(myCustomException.getErrorMessage());
      errResp.setRequestId(myCustomException.getRequestId());
      webClient
        .post(myCustomException.getPort(), myCustomException.getHost(), myCustomException.getUri())
        .as(BodyCodec.none())
        .sendJsonObject(JsonObject.mapFrom(errResp));
    }
  }

  private String getRequestId(JsonObject requestBody) {
    return requestBody.getString(Utility.REQUEST_ID_KEY);
  }

  private String getHost(JsonObject requestBody) {
    return requestBody.getString(Utility.HOST_KEY);
  }

  private Integer getPort(JsonObject requestBody) {
    return requestBody.getInteger(Utility.PORT_KEY);
  }


  private String getUri(JsonObject requestBody) {
    return requestBody.getString(Utility.URI_KEY);
  }

  private void sendAcknowledgementResponse(Message<JsonObject> request, String requestId) {
    var ackResponse = Utility.DEFAULT_ACK_RESPONSE.put(Utility.REQUEST_ID_KEY, requestId);
    request.reply(ackResponse);
  }

  private JsonObject createFailureCallback(JsonObject requestBody, String response) {
    return new JsonObject().put("status", "fail").put("response", response).put(Utility.REQUEST_ID_KEY, getRequestId(requestBody));
  }

  private JsonObject createFindAllPostsCallback(JsonObject requestBody, List<JsonObject> posts) {
    return new JsonObject().put("posts", posts).put(Utility.REQUEST_ID_KEY, getRequestId(requestBody));
  }


  private JsonObject createAddPostCallback(JsonObject requestBody, String post) {
    return new JsonObject().put("response", post).put(Utility.REQUEST_ID_KEY, getRequestId(requestBody));
  }

  private JsonObject createUpdatePostCallback(JsonObject requestBody, MongoClientUpdateResult response) {
    return new JsonObject().put("response", response).put(Utility.REQUEST_ID_KEY, getRequestId(requestBody));
  }

  private JsonObject createGetPostByIdCallback(JsonObject requestBody, List<JsonObject> posts) {

    var postList = posts
      .stream()
      .map(postJsonObject -> postJsonObject.mapTo(Post.class))
      .collect(Collectors.toList());

    return new JsonObject().put("posts", postList).put(Utility.REQUEST_ID_KEY, getRequestId(requestBody));
  }

  private JsonObject createDeletePostByIdCallback(JsonObject requestBody, MongoClientDeleteResult mongoClientDeleteResult) {

    return new JsonObject().put("response", mongoClientDeleteResult).put(Utility.REQUEST_ID_KEY, getRequestId(requestBody));
  }
}
