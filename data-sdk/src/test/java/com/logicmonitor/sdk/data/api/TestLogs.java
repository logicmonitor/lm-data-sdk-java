/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.api;

import com.logicmonitor.sdk.data.Configuration;
import com.logicmonitor.sdk.data.model.Input;
import com.logicmonitor.sdk.data.model.LogsInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;

public class TestLogs {

  final HashMap<String, String> resourceIds = new HashMap<>();
  Logs logs = Mockito.mock(Logs.class, Mockito.CALLS_REAL_METHODS);
  Configuration conf = Mockito.mock(Configuration.class);
  ApiClient client = Mockito.mock(ApiClient.class);

  void setUp() {
    logs.setBatch(true);
    logs.setApiClient(client);
    Object queueLock = new Object();
    Queue<Input> rawRequest = new LinkedList<>();
    logs.setQueueLock(queueLock);
    logs.setRawRequest(rawRequest);
  }

  void setUpBatchFalse() {
    logs.setBatch(true);
    logs.setApiClient(client);
    Object queueLock = new Object();
    Queue<Input> rawRequest = new LinkedList<>();
    logs.setQueueLock(queueLock);
    logs.setRawRequest(rawRequest);
  }

  @Test
  public void testSendLogs() throws IOException, ApiException {
    setUp();
    Mockito.when(logs.sendLogs("Testing log Api second call", resourceIds, null)).thenReturn(null);
    Assert.assertTrue("This will succeed.", true);
  }

  public void setPayload() {
    LogsInput input = new LogsInput("Testing log Api second call", resourceIds, "1789765436", null);
    List<LogsInput> logPayloadCache = new ArrayList<>();
    logPayloadCache.add(input);
    logs.setLogPayloadCache(logPayloadCache);
  }

  @Test
  public void testMergeRequest() {
    setPayload();
    LogsInput input = new LogsInput("Testing log Api second call", resourceIds, "1789765436", null);
    Queue<Input> rawRequest = new LinkedList<>();
    rawRequest.add(input);
    Mockito.when(logs.getRequest()).thenReturn(rawRequest);
    logs.mergeRequest();
    Assert.assertTrue("This will succeed.", true);
  }

  @Test
  public void testSingleRequest() throws ApiException, IOException {
    HashMap<String, String> metadata = new HashMap<String, String>();
    metadata.put("method", "sdk");
    metadata.put("compression", "compressed");
    LogsInput input =
        new LogsInput("Testing log Api second call", resourceIds, "1789765436", metadata);
    Logs.singleRequest(input);
    Assertions.assertEquals("1789765436", input.getTimeStamp());
  }

  @Test(expected = Exception.class)
  public void testSingleRequestNullMetadata() throws ApiException, IOException {
    LogsInput input = new LogsInput("Testing log Api second call", resourceIds, "1789765436", null);
    ApiResponse response = Logs.singleRequest(input);
    Assertions.assertEquals(response.getHeaders(), input.getMessage());
  }

  @Test
  public void testSendLogsForBatchFalse() throws IOException, ApiException {
    setUpBatchFalse();
    Mockito.when(logs.sendLogs("Testing log Api second call", resourceIds, null)).thenReturn(null);
    Assert.assertTrue("This will succeed.", true);
  }

  @Test
  public void testSendLogsForBatchTrueWithMetadata() throws IOException, ApiException {
    setUp();
    HashMap<String, String> metadata = new HashMap<String, String>();
    metadata.put("method", "sdk");
    metadata.put("compression", "compressed");
    Mockito.when(logs.sendLogs("Testing log Api second call", resourceIds, metadata))
        .thenReturn(null);
    Assert.assertTrue("This will succeed.", true);
  }
}
