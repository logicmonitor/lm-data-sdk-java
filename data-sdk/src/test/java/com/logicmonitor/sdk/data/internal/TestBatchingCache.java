/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.internal;

import com.logicmonitor.sdk.data.Configuration;
import com.logicmonitor.sdk.data.model.Input;
import com.logicmonitor.sdk.data.model.MetricsInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openapitools.client.ApiCallback;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;

@RunWith(MockitoJUnitRunner.class)
public class TestBatchingCache {

  BatchingCache batchingCache = Mockito.mock(BatchingCache.class, Mockito.CALLS_REAL_METHODS);
  Configuration conf = Mockito.mock(Configuration.class);
  ApiCallback apiCallback = Mockito.mock(ApiCallback.class);
  Map<String, List<String>> headers = new HashMap<>();

  @Test
  public void testMakeRequest() throws IOException {
    List<String> list = new ArrayList<>();
    ApiResponse<String> expected = null;
    try {
      list.add("body");
      expected =
          batchingCache.makeRequest(
              list, "/v2/metric/ingest", "POST", true, false, Configuration.getgZip());
    } catch (ApiException e) {
      Assertions.assertThrows(NullPointerException.class, (Executable) expected);
    }
  }

  void setQueue() {
    Object queueLock = new Object();
    Queue<Input> rawRequest = new LinkedList<>();
    batchingCache.setQueueLock(queueLock);
    batchingCache.setRawRequest(rawRequest);
  }

  @Test
  public void testAddRequest() {
    setQueue();
    Input input = new MetricsInput();
    batchingCache.addRequest(input);
    Assert.assertTrue("This will succeed.", true);
  }

  @Test
  public void testResponseHandler() {
    batchingCache.setApiCallback(apiCallback);
    headers.put("content-type", new ArrayList<>());
    ApiResponse response = new ApiResponse(202, headers);
    batchingCache.responseHandler(response);
    Assert.assertTrue("This will succeed.", true);
  }

  @Test
  public void testResponseHandlerForException() {
    batchingCache.setApiCallback(apiCallback);
    headers.put("content-type", new ArrayList<>());
    ApiResponse response = new ApiResponse(400, headers);
    batchingCache.responseHandler(response);
    Assert.assertFalse("This will fail.", false);
  }
}
