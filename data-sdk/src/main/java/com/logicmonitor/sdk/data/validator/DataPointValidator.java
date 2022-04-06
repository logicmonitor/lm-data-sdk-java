/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.validator;

import com.logicmonitor.sdk.data.model.DataPoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import lombok.Builder;

/** This Class is used check validation for Datapoint. */
@Builder
public class DataPointValidator implements AttributesValidator<DataPoint> {

  private static final String REGEX_DATA_POINT = "^[a-zA-Z_0-9]+$";

  private final Pattern patternDataPoint = Pattern.compile(REGEX_DATA_POINT);

  private final List<String> invalidDataPointNameSet =
      new ArrayList<>(
          Arrays.asList(
              "SIN",
              "COS",
              "LOG",
              "EXP",
              "FLOOR",
              "CEIL",
              "ROUND",
              "POW",
              "ABS",
              "SQRT",
              "RANDOM",
              "LT",
              "LE",
              "GT",
              "GE",
              "EQ",
              "NE",
              "IF",
              "MIN",
              "MAX",
              "LIMIT",
              "DUP",
              "EXC",
              "POP",
              "UN",
              "UNKN",
              "NOW",
              "TIME",
              "PI",
              "E",
              "AND",
              "OR",
              "XOR",
              "INF",
              "NEGINF",
              "STEP",
              "YEAR",
              "MONTH",
              "DATE",
              "HOUR",
              "MINUTE",
              "SECOND",
              "WEEK",
              "SIGN",
              "RND",
              "SUM2",
              "AVG2",
              "PERCENT",
              "RAWPERCENTILE",
              "IN",
              "NANTOZERO",
              "MIN2",
              "MAX2"));

  /**
   * @param dataPoint Used to check validation for datapoint
   * @return
   */
  @Override
  public String validator(DataPoint dataPoint) {
    String errorMsg = "";
    errorMsg += checkDataPointNameValidation(dataPoint.getName());
    errorMsg += checkDataPointAggregationTypeValidation(dataPoint.getAggregationType());
    errorMsg += checkDataPointDescriptionValidation(dataPoint.getDescription());
    errorMsg += checkDataPointTypeValidation(dataPoint.getType());
    errorMsg +=
        checkPercentileValue(
            dataPoint.getAggregationType(), dataPoint.getPercentileValue(), dataPoint.getName());
    return errorMsg;
  }

  /**
   * This Method is used to check validation for datapoint name.
   *
   * @param dataPointName
   * @return
   */
  protected String checkDataPointNameValidation(String dataPointName) {
    StringBuilder errorMsg = new StringBuilder();

    if (dataPointName == null) {
      errorMsg.append("Datapoint name is mandatory.");
    } else {
      String str = validDataPointName(dataPointName);
      if (!passEmptyAndSpellCheck(dataPointName)) {
        errorMsg.append("DataPoint Name Should not be empty or have tailing spaces.");
      } else if (dataPointName.length() > 128) {
        errorMsg.append("DataPoint Name size should not be greater than 128 characters.");
      } else if (str != null) {
        errorMsg.append(str);
      }
    }
    return errorMsg.toString();
  }

  /**
   * This Method is used to check validation for datapoint description.
   *
   * @param dataPointDescription
   * @return
   */
  protected String checkDataPointDescriptionValidation(String dataPointDescription) {
    StringBuilder errorMsg = new StringBuilder();

    if (dataPointDescription != null && (dataPointDescription.length() > 1024)) {
      errorMsg.append("Datapoint description should not be greater than 1024 characters.");
    }
    return errorMsg.toString();
  }

  /**
   * This Method is used to check validation for datapoint type.
   *
   * @param dataPointType
   * @return
   */
  protected String checkDataPointTypeValidation(String dataPointType) {
    StringBuilder errorMsg = new StringBuilder();

    if (dataPointType != null) {
      List<String> dataPointTypes = new ArrayList<>(Arrays.asList("counter", "gauge", "derive"));
      dataPointType = dataPointType.toLowerCase();
      int counter = 0;
      for (String type : dataPointTypes) {
        if (!dataPointType.equals(type)) {
          counter++;
        }
      }
      if (counter == 3) {
        errorMsg.append(
            String.format(
                "The datapoint type is having invalid dataPointType : %s.", dataPointType));
      }
    }
    return errorMsg.toString();
  }

  /**
   * This Method is used to check validation for datapoint AggregationType.
   *
   * @param dataPointAggregationType
   * @return
   */
  protected String checkDataPointAggregationTypeValidation(String dataPointAggregationType) {
    StringBuilder errorMsg = new StringBuilder();

    if (dataPointAggregationType != null) {
      List<String> dataPointAggregationTypes =
          new ArrayList<>(Arrays.asList("none", "avg", "sum", "percentile"));
      dataPointAggregationType = dataPointAggregationType.toLowerCase();
      int counter = 0;
      for (String type : dataPointAggregationTypes) {
        if (!dataPointAggregationType.equals(type)) {
          counter++;
        }
      }

      if (counter == 4) {
        errorMsg.append(
            String.format(
                "The datapoint aggregation type is having invalid datapoint aggregation Type : %s.",
                dataPointAggregationType));
      }
    }
    return errorMsg.toString();
  }

  protected boolean passEmptyAndSpellCheck(String name) {
    return name.length() != 0 && !name.startsWith(" ") && !name.endsWith(" ");
  }

  protected String validDataPointName(String name) {
    if (name == null) {
      return "Data point name cannot be null.";
    }
    if (!patternDataPoint.matcher(name).matches()) {
      return String.format("Invalid Data point name : %s.", name);
    }
    if (invalidDataPointNameSet.contains(name.toUpperCase())) {
      return String.format("%s is a keyword and cannot be use as datapoint name.", name);
    }
    return "";
  }

  protected String checkPercentileValue(
      String dataPointAggregationType, int percentileValue, String name) {
    String errorMsg = "";

    if (dataPointAggregationType != null
        && dataPointAggregationType.equalsIgnoreCase("percentile")) {
      if (percentileValue <= 0 || percentileValue >= 100) {
        errorMsg =
            String.format(
                "The datapoint %s is not provided or having invalid percentileValue, percentileValue should be between 0-100.",
                name);
      }
    }
    return errorMsg;
  }
}
