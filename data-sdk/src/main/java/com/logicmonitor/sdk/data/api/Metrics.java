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
import com.logicmonitor.sdk.data.model.*;
import com.logicmonitor.sdk.data.validator.DataSourceInstanceValidator;
import com.logicmonitor.sdk.data.validator.DataSourceValidator;
import com.logicmonitor.sdk.data.validator.ResourceValidator;
import com.logicmonitor.sdk.data.validator.Validator;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.client.ApiCallback;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.api.LmMetricIngestApi;
import org.openapitools.client.model.RestDataPointV1;
import org.openapitools.client.model.RestDataSourceInstanceV1;
import org.openapitools.client.model.RestMetricsV1;

/** This Class is used to Send Metrics.This class is used by user to interact with LogicMonitor. */
@Slf4j
public class Metrics extends BatchingCache {

  private static final String PATH = "/v2/metric/ingest";

  private static final String METHOD = "POST";
  private final ApiClientUserAgent userAgent = new ApiClientUserAgent();

  private ApiClient apiClient = new ApiClient();

  private Validator validator = new Validator();

  private ResourceValidator resourceValidator = new ResourceValidator();

  private DataSourceValidator dataSourceValidator = new DataSourceValidator();

  private DataSourceInstanceValidator dataSourceInstanceValidator =
      new DataSourceInstanceValidator();

  public Metrics() {
    this(Configuration.getConfiguration());
  }

  /**
   * @param conf This is configuration variable
   * @param interval This is interval
   * @param batch This attribute decides whether batch is true or false
   */
  public Metrics(final Configuration conf, final int interval, final boolean batch) {
    super(conf, interval, batch);
    apiClient.setBasePath(conf.setCompany());
  }

  /**
   * @param conf This is configuration variable
   * @param interval This is interval
   * @param batch This attribute decides whether batch is true or false
   * @param apiCallback ApiCallback Attribute
   */
  public Metrics(
      final Configuration conf, final int interval, final boolean batch, ApiCallback apiCallback) {
    super(conf, interval, batch, apiCallback);
    apiClient.setBasePath(conf.setCompany());
  }

  /** @param conf This is configuration variable */
  public Metrics(final Configuration conf) {
    super(conf);
    apiClient.setBasePath(conf.setCompany());
  }

  /**
   * @param input Metrics Input Variable
   * @return ApiResponse Retruen ApiResponse
   * @throws ApiException Throws ApiException
   */
  protected ApiResponse<String> singleRequest(MetricsInput input) throws ApiException, IOException {
    List<RestMetricsV1> listOfRestMetricsV1 = new ArrayList<>();
    BatchingCache batchingCache = new Metrics();
    List<RestDataPointV1> dataPoints = new ArrayList<>();

    RestDataPointV1 restDataPoint =
        new RestDataPointV1()
            .dataPointAggregationType(input.getDataPoint().getAggregationType())
            .dataPointDescription(input.getDataPoint().getDescription())
            .dataPointName(input.getDataPoint().getName())
            .dataPointType(input.getDataPoint().getType())
            .values(input.getValues())
            .percentileValue(input.getDataPoint().getPercentileValue());
    dataPoints.add(restDataPoint);

    List<RestDataSourceInstanceV1> instances = new ArrayList<>();

    RestDataSourceInstanceV1 restInstance =
        new RestDataSourceInstanceV1()
            .dataPoints(dataPoints)
            .instanceDescription(input.getDataSourceInstance().getDescription())
            .instanceDisplayName(input.getDataSourceInstance().getDisplayName())
            .instanceName(input.getDataSourceInstance().getName())
            .instanceProperties(input.getDataSourceInstance().getProperties());
    instances.add(restInstance);

    RestMetricsV1 restMetrics =
        new RestMetricsV1()
            .resourceIds(input.getResource().getIds())
            .resourceName(input.getResource().getName())
            .resourceProperties(input.getResource().getProperties())
            .resourceDescription(input.getResource().getDescription())
            .dataSource(input.getDataSource().getName())
            .dataSourceDisplayName(input.getDataSource().getDisplayName())
            .dataSourceGroup(input.getDataSource().getGroup())
            .dataSourceId(input.getDataSource().getId())
            .instances(instances);

    listOfRestMetricsV1.add(restMetrics);

    return batchingCache.makeRequest(
        listOfRestMetricsV1,
        PATH,
        METHOD,
        input.getResource().isCreate(),
        Configuration.getAsyncRequest(),
        Configuration.getgZip());
  }

  /**
   * @param resource This is variable for Resource properties.
   * @param dataSource This is variable for dataSource properties.
   * @param dataSourceInstance This is variable for dataSourceInstance properties.
   * @param dataPoint This is variable for dataPoint properties.
   * @param values This is variable for values properties.
   * @return Returns ApiResponse or null in case of singleRequest
   * @throws ApiException
   * @throws IOException
   */
  public Optional<ApiResponse<String>> sendMetrics(
      final Resource resource,
      final DataSource dataSource,
      final DataSourceInstance dataSourceInstance,
      final DataPoint dataPoint,
      final Map<String, String> values)
      throws ApiException, IOException {
    final String errorMsg =
        validator.validateAttributes(resource, dataSource, dataSourceInstance, dataPoint);
    if (errorMsg != null && errorMsg.length() > 0) {
      throw new IllegalArgumentException(errorMsg);
    }
    final LmMetricIngestApi metricApi = new LmMetricIngestApi(apiClient);
    metricApi.setCustomBaseUrl(apiClient.getBasePath());
    final MetricsInput input = new MetricsInput();
    input.setResource(resource);
    input.setDataSource(dataSource);
    input.setDataSourceInstance(dataSourceInstance);
    input.setDataPoint(dataPoint);

    for (final Entry<String, String> item : values.entrySet()) {
      input.getValues().put(item.getKey(), item.getValue());
    }
    if (batch) {
      addRequest(input);
      return null;
    } else {
      return Optional.ofNullable(singleRequest(input));
    }
  }

  /**
   * @param body Nested MAP as a body
   * @return
   */
  protected void createRestMetricsBody(
      final Map<
              Resource,
              Map<DataSource, Map<DataSourceInstance, Map<DataPoint, Map<String, String>>>>>
          body)
      throws IOException {

    final List<RestMetricsV1> listOfRestMetricsV1CreateTrue = new ArrayList<>();
    final List<RestMetricsV1> listOfRestMetricsV1CreateFalse = new ArrayList<>();
    final List<RestDataPointV1> dataPoints = new ArrayList<>();
    ApiResponse<String> response = null;

    Resource removeElement = null;
    RestMetricsV1 restMetrics = new RestMetricsV1();
    for (final Entry<
            Resource, Map<DataSource, Map<DataSourceInstance, Map<DataPoint, Map<String, String>>>>>
        item : body.entrySet()) {
      final Resource resource = item.getKey();
      removeElement = resource;
      DataSource dataSource = new DataSource();
      final List<RestDataSourceInstanceV1> instances = new ArrayList<>();

      for (final Entry<DataSource, Map<DataSourceInstance, Map<DataPoint, Map<String, String>>>>
          ds : item.getValue().entrySet()) {
        dataSource = ds.getKey();

        if (ds.getValue().entrySet().size() <= 100) {
          for (final Entry<DataSourceInstance, Map<DataPoint, Map<String, String>>> ins :
              ds.getValue().entrySet()) {
            final DataSourceInstance dataSourceInstance = ins.getKey();

            for (final Entry<DataPoint, Map<String, String>> dp : ins.getValue().entrySet()) {
              final DataPoint dataPoint = dp.getKey();
              final Map<String, String> valuePairs = new HashMap<>();
              for (final Entry<String, String> value : dp.getValue().entrySet()) {
                valuePairs.put(value.getKey(), value.getValue());
              }

              final RestDataPointV1 restDataPoint =
                  new RestDataPointV1()
                      .dataPointAggregationType(dataPoint.getAggregationType())
                      .dataPointDescription(dataPoint.getDescription())
                      .dataPointName(dataPoint.getName())
                      .dataPointType(dataPoint.getType())
                      .values(valuePairs)
                      .percentileValue(dataPoint.getPercentileValue());
              dataPoints.add(restDataPoint);
            }

            final RestDataSourceInstanceV1 restInstance =
                new RestDataSourceInstanceV1()
                    .dataPoints(dataPoints)
                    .instanceDescription(dataSourceInstance.getDescription())
                    .instanceDisplayName(dataSourceInstance.getDisplayName())
                    .instanceName(dataSourceInstance.getName())
                    .instanceProperties(dataSourceInstance.getProperties());
            instances.add(restInstance);
          }
        }
      }

      restMetrics =
          new RestMetricsV1()
              .resourceIds(resource.getIds())
              .resourceName(resource.getName())
              .resourceProperties(resource.getProperties())
              .resourceDescription(resource.getDescription())
              .dataSource(dataSource.getName())
              .dataSourceDisplayName(dataSource.getDisplayName())
              .dataSourceGroup(dataSource.getGroup())
              .singleInstanceDS(dataSource.getSingleInstanceDS())
              .dataSourceId(dataSource.getId())
              .instances(instances);

      if (resource.isCreate()) {
        listOfRestMetricsV1CreateTrue.add(restMetrics);
      } else {
        listOfRestMetricsV1CreateFalse.add(restMetrics);
      }
    }
    try {
      if (null != listOfRestMetricsV1CreateTrue && listOfRestMetricsV1CreateTrue.size() > 0) {
        response =
            makeRequest(
                listOfRestMetricsV1CreateTrue,
                PATH,
                METHOD,
                true,
                Configuration.getAsyncRequest(),
                Configuration.getgZip());
        responseHandler(response);
      }
      if (null != listOfRestMetricsV1CreateFalse && listOfRestMetricsV1CreateFalse.size() > 0) {
        response =
            makeRequest(
                listOfRestMetricsV1CreateFalse,
                PATH,
                METHOD,
                false,
                Configuration.getAsyncRequest(),
                Configuration.getgZip());
        responseHandler(response);
      }
    } catch (ApiException e) {
      apiCallback.onFailure(e, e.getCode(), e.getResponseHeaders());
    }
    getPayloadCache().remove(removeElement);
  }

  /** return void. */
  @Override
  protected void mergeRequest() {
    final MetricsInput singleRequest = (MetricsInput) getRequest().remove();
    if (!payloadCache.containsKey(singleRequest.getResource())) {
      payloadCache.put(singleRequest.getResource(), new HashMap<>());
    }
    final Map<DataSource, Map<DataSourceInstance, Map<DataPoint, Map<String, String>>>> dataSource =
        payloadCache.get(singleRequest.getResource());
    if (!dataSource.containsKey(singleRequest.getDataSource())) {
      dataSource.put(singleRequest.getDataSource(), new HashMap<>());
    }
    final Map<DataSourceInstance, Map<DataPoint, Map<String, String>>> instance =
        dataSource.get(singleRequest.getDataSource());
    if (!instance.containsKey(singleRequest.getDataSourceInstance())) {
      instance.put(singleRequest.getDataSourceInstance(), new HashMap<>());
    }
    final Map<DataPoint, Map<String, String>> dataPoint =
        instance.get(singleRequest.getDataSourceInstance());
    if (!dataPoint.containsKey(singleRequest.getDataPoint())) {
      dataPoint.put(singleRequest.getDataPoint(), new HashMap<>());
    }
    final Map<String, String> value = dataPoint.get(singleRequest.getDataPoint());

    for (final Entry<String, String> item : singleRequest.getValues().entrySet()) {
      value.put(item.getKey(), item.getValue());
    }
  }

  /**
   * @param resourceIds
   * @param resourceProperties
   * @param patch
   * @return ApiResponse
   * @throws ApiException
   */
  public ApiResponse<String> updateResourceProperties(
      Map<String, String> resourceIds, Map<String, String> resourceProperties, boolean patch)
      throws ApiException, IOException {
    BatchingCache batchingCache = new Metrics();

    String path = "/resource_property/ingest";
    String method = patch ? "PATCH" : "PUT";
    List<RestMetricsV1> listOfRestMetricsV1 = new ArrayList<>();

    if (resourceIds != null) {
      resourceValidator.checkResourceIdsValidation(resourceIds);
    }

    if (resourceProperties != null) {
      resourceValidator.checkResourcePropertiesValidation(resourceProperties);
    }
    RestMetricsV1 restMetrics =
        new RestMetricsV1().resourceIds(resourceIds).resourceProperties(resourceProperties);

    listOfRestMetricsV1.add(restMetrics);

    return batchingCache.makeRequest(
        listOfRestMetricsV1,
        path,
        method,
        false,
        Configuration.getAsyncRequest(),
        Configuration.getgZip());
  }

  /**
   * @param resourceIds
   * @param dataSourceName
   * @param dataSourceDisplayName
   * @param instanceName
   * @param instanceProperties
   * @param patch
   * @return ApiResponse
   * @throws ApiException
   */
  public ApiResponse<String> updateInstanceProperties(
      Map<String, String> resourceIds,
      String dataSourceName,
      String dataSourceDisplayName,
      String instanceName,
      Map<String, String> instanceProperties,
      boolean patch)
      throws ApiException, IOException {

    BatchingCache batchingCache = new Metrics();
    List<RestMetrics> restMetricsList = new ArrayList<>();
    String path = "/instance_property/ingest";
    String method = patch ? "PATCH" : "PUT";
    if (resourceIds != null) resourceValidator.checkResourceIdsValidation(resourceIds);
    if (dataSourceName != null) dataSourceValidator.checkDataSourceNameValidation(dataSourceName);
    if (instanceName != null) dataSourceInstanceValidator.checkInstanceNameValidation(instanceName);
    if (instanceProperties != null)
      dataSourceInstanceValidator.checkInstancePropertiesValidation(instanceProperties);

    RestMetrics restMetrics = new RestMetrics();
    restMetrics.setResourceIds(resourceIds);
    restMetrics.setDataSource(dataSourceName);
    restMetrics.setDataSourceDisplayName(dataSourceDisplayName);
    restMetrics.setInstanceName(instanceName);
    restMetrics.setInstanceProperties(instanceProperties);

    restMetricsList.add(restMetrics);

    return batchingCache.makeRequest(
        restMetricsList,
        path,
        method,
        false,
        Configuration.getAsyncRequest(),
        Configuration.getgZip());
  }

  /** return void. */
  @SneakyThrows
  @Override
  protected void doRequest() {
    createRestMetricsBody(payloadCache);
  }

  /** @param validator */
  public void setValidator(Validator validator) {
    this.validator = validator;
  }

  /** @param client */
  public void setApiClient(ApiClient client) {
    this.apiClient = client;
  }

  /** @param resourceValidator */
  public void setResourceValidator(ResourceValidator resourceValidator) {
    this.resourceValidator = resourceValidator;
  }

  /** @param dataSourceValidator */
  public void setDataSourceValidator(DataSourceValidator dataSourceValidator) {
    this.dataSourceValidator = dataSourceValidator;
  }

  /** @param dataSourceInstanceValidator */
  public void setDataSourceInstanceValidator(
      DataSourceInstanceValidator dataSourceInstanceValidator) {
    this.dataSourceInstanceValidator = dataSourceInstanceValidator;
  }
}
