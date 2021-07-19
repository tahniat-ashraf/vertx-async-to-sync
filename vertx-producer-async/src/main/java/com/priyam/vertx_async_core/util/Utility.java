package com.priyam.vertx_async_core.util;

import io.vertx.core.json.JsonObject;

public class Utility {

  public static final JsonObject DEFAULT_FAIL_MESSAGE = new JsonObject().put("status", "fail");
  public final static JsonObject DEFAULT_ACK_RESPONSE = new JsonObject().put("status", "success");

}
