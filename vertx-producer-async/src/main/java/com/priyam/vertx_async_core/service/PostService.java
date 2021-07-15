package com.priyam.vertx_async_core.service;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

import java.util.List;

public interface PostService {
  Single<List<JsonObject>> findAllPosts();
}
