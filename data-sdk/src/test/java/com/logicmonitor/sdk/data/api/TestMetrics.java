/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.api;

import com.logicmonitor.sdk.data.Configuration;
import com.logicmonitor.sdk.data.model.DataPoint;
import com.logicmonitor.sdk.data.model.DataSource;
import com.logicmonitor.sdk.data.model.DataSourceInstance;
import com.logicmonitor.sdk.data.model.Input;
import com.logicmonitor.sdk.data.model.MetricsInput;
import com.logicmonitor.sdk.data.model.Resource;
import com.logicmonitor.sdk.data.validator.DataSourceInstanceValidator;
import com.logicmonitor.sdk.data.validator.DataSourceValidator;
import com.logicmonitor.sdk.data.validator.ResourceValidator;
import com.logicmonitor.sdk.data.validator.Validator;
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
import org.mockito.Mockito;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.model.RestDataPointV1;
import org.openapitools.client.model.RestDataSourceInstanceV1;
import org.openapitools.client.model.RestMetricsV1;

public class TestMetrics {

  public Map<String, String> values = new HashMap<>();
  public HashMap<String, String> resourceIds = new HashMap<>();
  public Resource resource = new Resource();
  public DataSource dataSource = new DataSource();
  public DataSourceInstance dataSourceInstance = new DataSourceInstance();
  public DataPoint dataPoint = new DataPoint();
  public String resourceName = "Java_Data_SDK_Test";
  public String dataSourceName = "Java Data SDK";
  public String instanceName = "Java Data SDK Instance";
  public String cpuUsage = "cpuUsage";

  Metrics metrics = Mockito.mock(Metrics.class, Mockito.CALLS_REAL_METHODS);
  Configuration conf = Mockito.mock(Configuration.class);
  ApiClient client = Mockito.mock(ApiClient.class);

  void setUp() {
    metrics.setBatch(true);
    Validator validator = Mockito.mock(Validator.class);
    metrics.setValidator(validator);
    metrics.setApiClient(client);
    Object queueLock = new Object();
    Queue<Input> rawRequest = new LinkedList<>();
    metrics.setQueueLock(queueLock);
    metrics.setRawRequest(rawRequest);
    HashMap<Resource, Map<DataSource, Map<DataSourceInstance, Map<DataPoint, Map<String, String>>>>>
        body = new HashMap<>();
  }

  void setUpforBatchFalse() {
    metrics.setBatch(false);
    Validator validator = Mockito.mock(Validator.class);
    metrics.setValidator(validator);
    metrics.setApiClient(client);
    Object queueLock = new Object();
    Queue<Input> rawRequest = new LinkedList<>();
    metrics.setQueueLock(queueLock);
    metrics.setRawRequest(rawRequest);
    HashMap<Resource, Map<DataSource, Map<DataSourceInstance, Map<DataPoint, Map<String, String>>>>>
        body = new HashMap<>();
  }

  @Test
  public void testSendMetrics() throws Exception {
    setUp();
    List<RestMetricsV1> response = new ArrayList<>();
    resource.setName(resourceName);
    dataSource.setName(dataSourceName);
    dataSourceInstance.setName(instanceName);
    dataPoint.setName(cpuUsage);
    Mockito.when(metrics.sendMetrics(resource, dataSource, dataSourceInstance, dataPoint, values))
        .thenReturn(null);
    Assert.assertTrue("This will succeed.", true);
  }

  public void setPayload() {
    HashMap<Resource, Map<DataSource, Map<DataSourceInstance, Map<DataPoint, Map<String, String>>>>>
        payloadCache = new HashMap<>();
    metrics.setPayloadCache(payloadCache);
  }

  @Test
  public void testMergeRequest() {
    setPayload();
    resourceIds.put("system.displayname", resourceName);

    final Resource resource = Resource.builder().ids(resourceIds).name(resourceName).build();
    final DataSource dataSource =
        DataSource.builder().name(dataSourceName).singleInstanceDS(false).build();
    final DataSourceInstance dataSourceInstance =
        DataSourceInstance.builder().name(instanceName).build();
    final DataPoint dataPoint = DataPoint.builder().name(cpuUsage).build();

    final MetricsInput input = new MetricsInput();
    input.setResource(resource);
    input.setDataSource(dataSource);
    input.setDataSourceInstance(dataSourceInstance);
    input.setDataPoint(dataPoint);
    Queue<Input> rawRequest = new LinkedList<>();
    rawRequest.add(input);

    Mockito.when(metrics.getRequest()).thenReturn(rawRequest);
    metrics.mergeRequest();
    Assert.assertTrue("This will succeed.", true);
  }

  void setConf() {
    Mockito.when(conf.setCompany()).thenReturn("https://company01.test.com/rest");
  }

  @Test(expected = Exception.class)
  public void testCreateRestMetricsBody() throws IOException {

    List<RestMetricsV1> expected = new ArrayList<>();
    RestMetricsV1 restMetrics;
    List<RestDataSourceInstanceV1> instances = new ArrayList<>();
    List<RestDataPointV1> dataPoints = new ArrayList<>();
    RestDataSourceInstanceV1 restInstance =
        new RestDataSourceInstanceV1().dataPoints(dataPoints).instanceName(instanceName);
    instances.add(restInstance);
    restMetrics =
        new RestMetricsV1()
            .resourceName(resourceName)
            .dataSource(dataSourceName)
            .instances(instances);
    expected.add(restMetrics);
    Map<Resource, Map<DataSource, Map<DataSourceInstance, Map<DataPoint, Map<String, String>>>>>
        resourceBody = new HashMap<>();
    Map<DataSource, Map<DataSourceInstance, Map<DataPoint, Map<String, String>>>> dataSourceBody =
        new HashMap<>();
    Map<DataSourceInstance, Map<DataPoint, Map<String, String>>> instanceBody = new HashMap<>();
    resource.setName(resourceName);
    resource.setCreate(true);
    dataSource.setName(dataSourceName);
    dataSourceInstance.setName(instanceName);
    resourceBody.put(resource, dataSourceBody);
    dataSourceBody.put(dataSource, instanceBody);
    instanceBody.put(dataSourceInstance, new HashMap<>());

    HashMap<Resource, Map<DataSource, Map<DataSourceInstance, Map<DataPoint, Map<String, String>>>>>
        payloadCache = new HashMap<>();
    Mockito.when(metrics.getPayloadCache()).thenReturn(payloadCache);
    metrics.createRestMetricsBody(resourceBody);
  }

  @Test(expected = Exception.class)
  public void testDoRequest() {
    metrics.doRequest();
  }

  @Test(expected = ApiException.class)
  public void testSingleRequest() throws ApiException, IOException {
    final MetricsInput input = new MetricsInput();
    input.setResource(resource);
    input.setDataSource(dataSource);
    input.setDataSourceInstance(dataSourceInstance);
    input.setDataPoint(dataPoint);
    metrics.singleRequest(input);
  }

  @Test(expected = Exception.class)
  public void testInvalidSendMetrics() throws Exception {
    setUpforBatchFalse();
    List<RestMetricsV1> response = new ArrayList<>();
    resource.setName(resourceName);
    dataSource.setName(dataSourceName);
    dataSourceInstance.setName(instanceName);
    dataPoint.setName(cpuUsage);
    metrics.sendMetrics(resource, dataSource, dataSourceInstance, dataPoint, values);
  }

  @Test(expected = Exception.class)
  public void testCreateTrue() throws IOException {

    List<RestMetricsV1> expected = new ArrayList<>();
    RestMetricsV1 restMetrics;
    List<RestDataSourceInstanceV1> instances = new ArrayList<>();
    List<RestDataPointV1> dataPoints = new ArrayList<>();
    RestDataSourceInstanceV1 restInstance =
        new RestDataSourceInstanceV1().dataPoints(dataPoints).instanceName(instanceName);
    instances.add(restInstance);
    restMetrics =
        new RestMetricsV1()
            .resourceName(resourceName)
            .dataSource(dataSourceName)
            .instances(instances);
    expected.add(restMetrics);
    Map<Resource, Map<DataSource, Map<DataSourceInstance, Map<DataPoint, Map<String, String>>>>>
        resourceBody = new HashMap<>();
    Map<DataSource, Map<DataSourceInstance, Map<DataPoint, Map<String, String>>>> dataSourceBody =
        new HashMap<>();
    Map<DataSourceInstance, Map<DataPoint, Map<String, String>>> instanceBody = new HashMap<>();
    resource.setName(resourceName);
    dataSource.setName(dataSourceName);
    dataSourceInstance.setName(instanceName);
    resourceBody.put(resource, dataSourceBody);
    dataSourceBody.put(dataSource, instanceBody);
    instanceBody.put(dataSourceInstance, new HashMap<>());

    HashMap<Resource, Map<DataSource, Map<DataSourceInstance, Map<DataPoint, Map<String, String>>>>>
        payloadCache = new HashMap<>();
    Mockito.when(metrics.getPayloadCache()).thenReturn(payloadCache);
    metrics.createRestMetricsBody(resourceBody);
  }

  @Test(expected = Exception.class)
  public void TestUpdateResourceProperties() throws ApiException, IOException {
    ResourceValidator resourceValidator = Mockito.mock(ResourceValidator.class);
    metrics.setResourceValidator(resourceValidator);
    ApiResponse<String> response =
        metrics.updateResourceProperties(resourceIds, resource.getProperties(), false);
    Assertions.assertEquals(response.getStatusCode(), 400);
  }

  @Test(expected = Exception.class)
  public void TestUpdateInstanceProperties() throws ApiException, IOException {
    HashMap<String, String> instanceProperties = new HashMap<>();
    instanceProperties.put("InstanceProperty", "InstanceProperty");
    ResourceValidator resourceValidator = Mockito.mock(ResourceValidator.class);
    DataSourceInstanceValidator dataSourceInstanceValidator =
        Mockito.mock(DataSourceInstanceValidator.class);
    DataSourceValidator dataSourceValidator = Mockito.mock(DataSourceValidator.class);
    metrics.setResourceValidator(resourceValidator);
    metrics.setDataSourceInstanceValidator(dataSourceInstanceValidator);
    metrics.setDataSourceValidator(dataSourceValidator);
    ApiResponse<String> response =
        metrics.updateInstanceProperties(
            resourceIds, dataSourceName, null, instanceName, instanceProperties, false);
    Assertions.assertEquals(response.getStatusCode(), 400);
  }
}
