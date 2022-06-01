/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.api;

import com.logicmonitor.sdk.data.ApiClientUserAgent;
import com.logicmonitor.sdk.data.Configuration;
import com.logicmonitor.sdk.data.internal.BatchingCache;
import com.logicmonitor.sdk.data.model.LogsInput;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import lombok.SneakyThrows;
import org.openapitools.client.*;

/** This Class is used to Send Logs.This class is used by user to interact with LogicMonitor. */
public class Logs extends BatchingCache {

  private static final String PATH = "/log/ingest";

  private static final String METHOD = "POST";
  private final ApiClientUserAgent userAgent = new ApiClientUserAgent();

  private ApiClient apiClient = new ApiClient();

  public Logs() {
    this(Configuration.getConfiguration());
  }

  public Logs(final Configuration conf, final int interval, final boolean batch) {
    super(conf, interval, batch);
    apiClient.setBasePath(Configuration.setCompany());
  }

  /**
   * @param conf This is configuration variable
   * @param interval This is interval
   * @param batch This attribute decides whether batch is true or false
   * @param apiCallback This is ApiCallback variable
   */
  public Logs(
      final Configuration conf, final int interval, final boolean batch, ApiCallback apiCallback) {
    super(conf, interval, batch, apiCallback);
    apiClient.setBasePath(Configuration.setCompany());
  }

  /** @param conf This is configuration variable */
  public Logs(final Configuration conf) {
    super(conf);
    apiClient.setBasePath(Configuration.setCompany());
  }

  /**
   * @param logsV1 This logInput attribute
   * @return
   * @throws ApiException
   */
  protected static ApiResponse singleRequest(final LogsInput logsV1)
      throws ApiException, IOException {

    final List<Map<String, Object>> logBody = new ArrayList<>();
    final Map<String, Object> body = new HashMap<>();
    body.put("message", logsV1.getMessage());
    body.put("_lm.resourceId", logsV1.getResourceId());
    body.put("timestamp", logsV1.getTimeStamp());
    if (logsV1.getMetadata() != null) {
      for (Map.Entry<String, String> entry : logsV1.getMetadata().entrySet()) {
        body.put(entry.getKey(), entry.getValue());
      }
    } else {
      body.put("metadata", logsV1.getMetadata());
    }
    logBody.add(body);
    final BatchingCache b = new Logs();
    return b.makeRequest(logBody, PATH, METHOD, true, false, Configuration.getgZip());
  }

  /** Return void */
  @Override
  protected void mergeRequest() {
    final LogsInput singleRequest = (LogsInput) getRequest().remove();
    logPayloadCache.add(singleRequest);
  }

  /** Return void */
  @SneakyThrows
  @Override
  protected void doRequest() {
    final List<ApiResponse<String>> responseList = new ArrayList<>();
    List<Map<String, Object>> list = createBody();
    ApiResponse<String> response;

    try {
      if (null != list && list.size() > 0) {
        response = makeRequest(list, PATH, METHOD, true, false, Configuration.getgZip());
        responseList.add(response);
        responseHandler(response);
      }
    } catch (ApiException e) {
      apiCallback.onFailure(e, e.getCode(), e.getResponseHeaders());
    }
  }

  /**
   * @param message
   * @param resourceId
   * @param metadata
   * @return
   * @throws IOException
   * @throws ApiException
   */
  public Optional<ApiResponse> sendLogs(
      final String message,
      final Map<String, String> resourceId,
      final Map<String, String> metadata)
      throws IOException, ApiException {
    final String timeStamp = String.valueOf(Instant.now().getEpochSecond());
    final LogsInput logsV1 = new LogsInput(message, resourceId, timeStamp, metadata);

    if (batch) {
      addRequest(logsV1);
      return null;
    }
    return Optional.ofNullable(singleRequest(logsV1));
  }

  /** @return List<Map < String, Object>> */
  private List<Map<String, Object>> createBody() {
    final List<Map<String, Object>> logBody = new ArrayList<>();
    for (final LogsInput logsV1 : logPayloadCache) {
      final Map<String, Object> body = new HashMap<>();
      body.put("message", logsV1.getMessage());
      body.put("_lm.resourceId", logsV1.getResourceId());
      body.put("timestamp", logsV1.getTimeStamp());
      if (logsV1.getMetadata() != null) {
        for (Map.Entry<String, String> entry : logsV1.getMetadata().entrySet()) {
          body.put(entry.getKey(), entry.getValue());
        }
      } else {
        body.put("metadata", logsV1.getMetadata());
      }
      logBody.add(body);
    }
    logPayloadCache.clear();
    return logBody;
  }

  /** @param client */
  public void setApiClient(ApiClient client) {
    this.apiClient = client;
  }
}
