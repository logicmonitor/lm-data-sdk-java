/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.internal;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.logicmonitor.sdk.data.Configuration;
import com.logicmonitor.sdk.data.model.*;
import java.lang.reflect.Type;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import org.openapitools.client.*;

/** This Class is used send multiple request . */
@Getter
@Setter
@Slf4j
public abstract class BatchingCache {

  private static final String PATH = "/v2/metric/ingest";

  protected Queue<Input> rawRequest = new LinkedList<>();
  protected HashMap<
          Resource, Map<DataSource, Map<DataSourceInstance, Map<DataPoint, Map<String, String>>>>>
      payloadCache = new HashMap<>();
  protected List<LogsInput> logPayloadCache = new ArrayList<>();
  protected boolean batch;
  protected ApiCallback apiCallback;
  private long lastTimeSend;
  private Thread mergeThread;
  private Thread requestThread;
  private Object queueLock = new Object();
  private Object cacheLock = new Object();
  private int interval = 0;

  /** @param conf This is configuration variable */
  public BatchingCache(final Configuration conf) {
    this(conf, 0, false);
  }

  /**
   * @param conf
   * @param interval
   * @param batch
   */
  public BatchingCache(final Configuration conf, final int interval, final boolean batch) {
    this.interval = interval;
    this.batch = batch;
    checkBatch();
  }

  /**
   * @param conf
   * @param interval
   * @param batch
   * @param responseCallback
   */
  public BatchingCache(
      final Configuration conf,
      final int interval,
      final boolean batch,
      ApiCallback responseCallback) {
    this.interval = interval;
    this.batch = batch;
    apiCallback = responseCallback;

    checkBatch();
  }

  /** @param response */
  protected void responseHandler(ApiResponse response) {
    ApiException apiException = new ApiException();
    log.debug(
        "Response is {0}  {1}  \n{2}",
        response, response.getStatusCode(), response.getHeaders().toString());

    if (response != null) {
      if (response.getStatusCode() == 200
          || response.getStatusCode() == 202
          || response.getStatusCode() == 207)
        apiCallback.onSuccess(response, response.getStatusCode(), response.getHeaders());
      if (response.getStatusCode() >= 300)
        apiCallback.onFailure(apiException, response.getStatusCode(), response.getHeaders());
    }
  }

  /** Abstract Method. */
  protected abstract void mergeRequest();

  /** Abstract Method. */
  protected abstract void doRequest();

  /** @param body */
  public void addRequest(final Input body) {
    synchronized (queueLock) {
      rawRequest.add(body);
    }
  }

  /** Merging all the Request while Batching. */
  @SneakyThrows
  public void commonMergeRequest() {
    while (true) {
      if (getRequest().size() > 0) {
        synchronized (queueLock) {
          synchronized (cacheLock) {
            try {
              this.mergeRequest();
            } catch (Exception e) {
              log.error("Exception:", e);
            }
          }
        }
      } else {
        Thread.sleep(1000);
      }
    }
  }

  /** Called while Batching. */
  @SneakyThrows
  public void commonDoRequest() {
    while (true) {
      long currentTime = System.currentTimeMillis();
      if (currentTime > (lastTimeSend + (interval * 1000))) {
        synchronized (cacheLock) {
          try {
            this.doRequest();
          } catch (Exception e) {
            log.error("Exception: ", e);
          }
        }
        lastTimeSend = currentTime;
      } else {
        Thread.sleep(1000);
      }
    }
  }

  /**
   * @param body
   * @param path
   * @param method
   * @param create
   * @param async
   * @return
   * @throws ApiException
   */
  public ApiResponse<String> makeRequest(
      final List body, final String path, final String method, final boolean create, boolean async)
      throws ApiException {

    final ApiClient apiClient = new ApiClient();
    final Pair pair = new Pair("create", String.valueOf(create));
    final List<Pair> queryParams = new ArrayList<>();
    final List<Pair> collectionQueryParams = new ArrayList<>();
    if (create && path == PATH) {
      queryParams.add(pair);
    }
    final Map<String, String> headersParams = new HashMap<>();
    final Map<String, String> cookieParams = new HashMap<>();
    final Map<String, Object> formParams = new HashMap<>();
    final String[] authSetting = {"LMv1"};
    final String[] localVarContentTypes = {"application/json"};

    headersParams.put("Accept", apiClient.selectHeaderContentType(localVarContentTypes));
    headersParams.put("Content-Type", apiClient.selectHeaderContentType(localVarContentTypes));
    headersParams.put(
        "Authorization", Configuration.getAuthToken(new Gson().toJson(body), method, path));

    log.debug("Request: " + new Gson().toJson(body));

    final String companyUrl = Configuration.setCompany();
    apiClient.setBasePath(companyUrl);
    Call call =
        apiClient.buildCall(
            apiClient.getBasePath(),
            path,
            method,
            queryParams,
            collectionQueryParams,
            body,
            headersParams,
            cookieParams,
            formParams,
            authSetting,
            apiCallback);
    Type localVarReturnType = new TypeToken<String>() {}.getType();

    ApiResponse<String> syncReponse = null;
    try {
      syncReponse = apiClient.execute(call, localVarReturnType);
    } catch (ApiException e) {
      throw new ApiException(e.getCode() + " " + e.getMessage() + " " + e.getResponseBody());
    }
    return syncReponse;
  }

  /** @return queue. */
  public Queue<Input> getRequest() {
    return rawRequest;
  }

  /** Initialising Multithreading. */
  private void checkBatch() {
    if (batch) {
      mergeThread = new Thread(this::commonMergeRequest);
      mergeThread.setDaemon(true);
      mergeThread.start();

      requestThread = new Thread(this::commonDoRequest);
      requestThread.setDaemon(true);
      requestThread.start();
    }
  }
}
