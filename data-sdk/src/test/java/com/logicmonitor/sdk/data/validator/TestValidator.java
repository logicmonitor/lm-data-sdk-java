/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */

package com.logicmonitor.sdk.data.validator;

import com.logicmonitor.sdk.data.model.DataPoint;
import com.logicmonitor.sdk.data.model.DataSource;
import com.logicmonitor.sdk.data.model.DataSourceInstance;
import com.logicmonitor.sdk.data.model.Resource;
import java.util.HashMap;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class TestValidator {

  public Resource resource = new Resource();
  public DataSource dataSource = new DataSource();
  public DataSourceInstance dataSourceInstance = new DataSourceInstance();
  public DataPoint dataPoint = new DataPoint();
  public String resourceName = "Java_Data_SDK_Test";
  public String dataSourceName = "Java Data SDK";
  public String instanceName = "Java Data SDK Instance";
  public String cpuUsage = "cpuUsage";

  Validator validator = new Validator();

  @Test
  public void testValidator() {
    String msg =
        "Instance Properties Should not contain System or auto properties : system.property.Invalid instance name : Java Data SDK Instance.";
    resource.setName(resourceName);
    HashMap<String, String> resourceIds = new HashMap<>();
    resourceIds.put("system.displayname", "Resource-display-name");
    HashMap<String, String> datasourceInstanceProperty = new HashMap<>();
    datasourceInstanceProperty.put("system.property", "Instance-property");
    resource.setIds(resourceIds);
    dataSource.setName(dataSourceName);
    dataSource.setSingleInstanceDS(false);
    dataSource.setDisplayName("DatasourceDisplayName");
    dataSource.setId(123);
    dataSourceInstance.setName(instanceName);
    dataSourceInstance.setDescription("InstanceDescription");
    dataSourceInstance.setDisplayName("InstanceName");
    dataSourceInstance.setId(123);
    dataSourceInstance.setProperties(datasourceInstanceProperty);
    dataPoint.setName(cpuUsage);
    dataPoint.setAggregationType("percentile");
    dataPoint.setPercentileValue(12);
    dataPoint.setDescription("DatapointDescription");
    String errorMsg =
        validator.validateAttributes(resource, dataSource, dataSourceInstance, dataPoint);
    Assertions.assertEquals(msg, errorMsg);
  }
}
