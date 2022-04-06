/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetricsInput implements Input {

  private Resource resource;
  private DataSource dataSource;
  private DataSourceInstance dataSourceInstance;
  private DataPoint dataPoint;
  private Map<String, String> values;

  public MetricsInput() {
    resource = new Resource();
    dataSource = new DataSource();
    dataSourceInstance = new DataSourceInstance();
    dataPoint = new DataPoint();
    values = new HashMap<>();
  }
}
