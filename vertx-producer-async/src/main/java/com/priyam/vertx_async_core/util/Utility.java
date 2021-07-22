package com.priyam.vertx_async_core.util;

import io.vertx.core.json.JsonObject;

public class Utility {

  public static final JsonObject DEFAULT_FAIL_MESSAGE = new JsonObject().put("status", "fail");
  public final static JsonObject DEFAULT_ACK_RESPONSE = new JsonObject().put("status", "success");
  public final static String REQUEST_ID_KEY = "requestId";
  public final static String HOST_KEY = "host";
  public final static String PORT_KEY = "port";
  public final static String URI_KEY = "uri";

}
