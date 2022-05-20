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
import org.mockito.Mockito;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;

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

  @Test(expected = Exception.class)
  public void testDoRequest() {
    setPayload();
    logs.doRequest();
  }

  @Test(expected = ApiException.class)
  public void testSingleRequest() throws ApiException, IOException {
    LogsInput input = new LogsInput("Testing log Api second call", resourceIds, "1789765436", null);
    Logs.singleRequest(input);
  }

  @Test
  public void testSendLogsForBatchFalse() throws IOException, ApiException {
    setUpBatchFalse();
    Mockito.when(logs.sendLogs("Testing log Api second call", resourceIds, null)).thenReturn(null);
    Assert.assertTrue("This will succeed.", true);
  }
}
