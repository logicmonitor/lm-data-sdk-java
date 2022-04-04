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
import lombok.NoArgsConstructor;

/** This Class is used check validation. */
@NoArgsConstructor
public class Validator {

  AttributesValidator<Resource> resourceValidator = new ResourceValidator();
  AttributesValidator<DataSource> dataSourceValidator = new DataSourceValidator();
  AttributesValidator<DataSourceInstance> dataSourceInstanceValidator =
      new DataSourceInstanceValidator();
  AttributesValidator<DataPoint> dataPointValidator = new DataPointValidator();

  /**
   * @param resource This is variable for Resource properties.
   * @param dataSource This is variable for dataSource properties.
   * @param instance This is variable for instance properties.
   * @param dataPoint This is variable for dataPoint properties.
   * @return String
   */
  public String validateAttributes(
      Resource resource, DataSource dataSource, DataSourceInstance instance, DataPoint dataPoint) {
    String errorMsg = "";
    errorMsg += resourceValidator.validator(resource);
    errorMsg += dataSourceValidator.validator(dataSource);
    if (!dataSource.getSingleInstanceDS()) {
      errorMsg += dataSourceInstanceValidator.validator(instance);
    }
    errorMsg += dataPointValidator.validator(dataPoint);
    return errorMsg;
  }
}
