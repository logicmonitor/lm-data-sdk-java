/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.validator;

import com.logicmonitor.sdk.data.model.DataPoint;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class TestDataPointValidator {

  DataPointValidator dataPointValidator = new DataPointValidator();

  @Test
  public void providedNullDataPointName() {
    String expected = "Datapoint name is mandatory.";
    Assertions.assertEquals(expected, dataPointValidator.checkDataPointNameValidation(null));
  }

  @Test
  public void providedSpaceToDataPointName() {
    String expected = "DataPoint Name Should not be empty or have tailing spaces.";
    Assertions.assertEquals(
        expected, dataPointValidator.checkDataPointNameValidation(" datapointname"));
  }

  @Test
  public void testValidDataPointName() {
    String expected = "Invalid Data point name : @datapointname.";
    Assertions.assertEquals(expected, dataPointValidator.validDataPointName("@datapointname"));
  }

  @Test
  public void testInvalidDataPointNameSet() {
    String expected = "COS is a keyword and cannot be use as datapoint name.";
    Assertions.assertEquals(expected, dataPointValidator.validDataPointName("COS"));
  }

  @Test
  public void testCheckDataPointDescriptionValidation() {
    char[] chars = new char[1026];
    String description = new String(chars);
    String expected = "Datapoint description should not be greater than 1024 characters.";
    Assertions.assertEquals(
        expected, dataPointValidator.checkDataPointDescriptionValidation(description));
  }

  @Test
  public void testCheckDataPointTypeValidation() {
    String expected = "The datapoint type is having invalid dataPointType : test.";
    Assertions.assertEquals(expected, dataPointValidator.checkDataPointTypeValidation("test"));
  }

  @Test
  public void testCheckDataPointAggregationTypeValidation() {
    String expected =
        "The datapoint aggregation type is having invalid datapoint aggregation Type : add.";
    Assertions.assertEquals(
        expected, dataPointValidator.checkDataPointAggregationTypeValidation("add"));
  }

  @Test
  public void testPassEmptyAndSpellCheck() {
    Assertions.assertEquals(false, dataPointValidator.passEmptyAndSpellCheck(" "));
  }

  @Test
  public void testDatapointBuilder() {
    DataPoint.builder()
        .description("DatapointDescription")
        .aggregationType("percentile")
        .percentileValue(12)
        .build();
    Assertions.assertTrue(true);
  }
}
