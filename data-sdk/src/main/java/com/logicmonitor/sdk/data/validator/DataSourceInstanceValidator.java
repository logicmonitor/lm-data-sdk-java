/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.validator;

import com.logicmonitor.sdk.data.model.DataSourceInstance;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** This Class is used check validation for DataSourceInstance . */
@Builder
@NoArgsConstructor
public class DataSourceInstanceValidator implements AttributesValidator<DataSourceInstance> {

  private static final String REGEX_INSTANCE_NAME = "^[a-z:A-Z0-9\\._\\-]+$";
  private static final String REGEX_INSTANCE_ID_9_DIGIT = "^[0-9]{0,9}$";
  private static final String REGEX_INSTANCE_ID_EXPO = "^e\\^\\-?\\d*?$";
  private static final String REGEX_INVALID_DEVICE_DISPLAY_NAME = "[*<?,;`\\n]";

  private final Pattern patternInstanceName = Pattern.compile(REGEX_INSTANCE_NAME);
  private final Pattern patternInstanceId9Digit = Pattern.compile(REGEX_INSTANCE_ID_9_DIGIT);
  private final Pattern patternInstanceIdExpo = Pattern.compile(REGEX_INSTANCE_ID_EXPO);
  private final Pattern patternInvalidDeviceDisplayName =
      Pattern.compile(REGEX_INVALID_DEVICE_DISPLAY_NAME);

  @Override
  public String validator(DataSourceInstance instance) {
    String errorMsg = "";
    Integer instanceId = instance.getId();
    if (instanceId != null) {
      if (instanceId > 0) {
        errorMsg += checkInstanceId(instanceId);
      } else {
        errorMsg += String.format("Instance Id %d should not be negative.", instanceId);
      }
    } else {
      if (instance.getName() == null) {
        errorMsg += "Either Instance Name or Instance Id is Mandatory.";
      }
    }
    if (instance.getDisplayName() != null) {
      errorMsg += checkInstanceDisplayNameValidation(instance.getDisplayName());
    }
    if (instance.getProperties() != null) {
      errorMsg += checkInstancePropertiesValidation(instance.getProperties());
    }
    if (instance.getName() != null) {
      errorMsg += checkInstanceNameValidation(instance.getName());
    }
    return errorMsg;
  }

  /**
   * This Method is used to check validation for DataSourceInstance Name.
   *
   * @param instanceName
   * @return
   */
  public String checkInstanceNameValidation(String instanceName) {
    StringBuilder errorMsg = new StringBuilder();

    if (passEmptyAndSpellCheck(instanceName)) {
      errorMsg.append("Instance Name Should not be empty or have tailing spaces.");
    } else if (isNameLengthValid(instanceName)) {
      errorMsg.append("Instance Name size should not be greater than 255 characters.");
    } else if (!isValidInstanceName(instanceName)) {
      errorMsg.append(String.format("Invalid instance name : %s.", instanceName));
    }
    return errorMsg.toString();
  }

  /**
   * This Method is used to check validation for DataSourceInstance DisplayName.
   *
   * @param instanceDisplayName
   * @return
   */
  protected String checkInstanceDisplayNameValidation(String instanceDisplayName) {
    StringBuilder errorMsg = new StringBuilder();

    if (instanceDisplayName != null) {
      if (passEmptyAndSpellCheck(instanceDisplayName)) {
        errorMsg.append("Instance display name Should not be empty or have tailing spaces.");
      } else if (isNameLengthValid(instanceDisplayName)) {
        errorMsg.append("Instance display name size should not be greater than 255 characters.");
      } else {
        StringBuilder invalidName = validateDeviceDisplayName(instanceDisplayName);
        errorMsg.append(invalidName);
      }
    }
    return errorMsg.toString();
  }

  /**
   * This Method is used to check validation for DataSourceInstance Properties.
   *
   * @param instanceProperties
   * @return
   */
  public String checkInstancePropertiesValidation(Map<String, String> instanceProperties) {
    StringBuilder errorMsg = new StringBuilder();

    if (instanceProperties.size() > 0) {
      for (Map.Entry<String, String> item : instanceProperties.entrySet()) {
        if (passEmptyAndSpellCheck(item.getKey())) {
          errorMsg.append(
              "Instance Properties Key should not be null, empty or have trailing spaces.");
        } else if (isNameLengthValid(item.getKey())) {
          errorMsg.append("Instance Properties Key should not be greater than 255 characters.");
        } else if (item.getKey().toLowerCase().startsWith("system.")
            || item.getKey().toLowerCase().startsWith("auto.")) {
          errorMsg.append(
              String.format(
                  "Instance Properties Should not contain System or auto properties : %s.",
                  item.getKey()));
        } else if (isInValidId(item.getKey())) {
          errorMsg.append(String.format("Invalid instance Properties key : %s.", item.getKey()));
        } else if (passEmptyAndSpellCheck(item.getValue())) {
          errorMsg.append(
              "Instance Properties Value should not be null, empty or have trailing spaces.");
        } else if (item.getValue().length() > 24000) {
          errorMsg.append("Instance Properties Value should not be greater than 24000 characters.");
        } else if (isInValidId(item.getValue())) {
          errorMsg.append(
              String.format(
                  "Invalid instance properties value : %s for Key : %s.",
                  item.getValue(), item.getKey()));
        }
      }
    }
    return errorMsg.toString();
  }

  /**
   * This Method is used to check validation for DataSourceInstance Id.
   *
   * @param instanceId
   * @return
   */
  protected String checkInstanceId(int instanceId) {
    StringBuilder errorMsg = new StringBuilder();
    if (!isValidInstanceID9Digit(instanceId)) {
      errorMsg.append("Instance Id cannot be more than 9 digit.");
    }
    if (isValidInstanceIdExpo(instanceId)) {
      errorMsg.append("Instance Id cannot be in Exponential form.");
    }
    return errorMsg.toString();
  }

  protected boolean passEmptyAndSpellCheck(String name) {
    return name.length() == 0 || name.startsWith(" ") || name.endsWith(" ");
  }

  protected boolean isNameLengthValid(String name) {
    return name.length() > 255;
  }

  protected boolean isValidInstanceName(String instanceName) {
    return patternInstanceName.matcher(instanceName).matches();
  }

  protected StringBuilder validateDeviceDisplayName(String name) {
    StringBuilder errorMsg = new StringBuilder();
    if (name == null) {
      errorMsg.append("Instance display name can't be empty.");
    }
    if (name.startsWith(" ") || name.endsWith(" ")) {
      errorMsg.append("Space is not allowed at start and end in instance display name.");
    }
    Matcher matcher = patternInvalidDeviceDisplayName.matcher(name);
    if (matcher.find()) {
      errorMsg.append(String.format("Invalid instance display name : %s.", name));
    }
    return errorMsg;
  }

  protected boolean isInValidId(String instanceId) {
    return !patternInstanceName.matcher(instanceId).matches();
  }

  protected boolean isValidInstanceID9Digit(int instanceId) {
    return patternInstanceId9Digit.matcher(String.valueOf(instanceId)).matches();
  }

  protected boolean isValidInstanceIdExpo(int instanceId) {
    return patternInstanceIdExpo.matcher(String.valueOf(instanceId)).matches();
  }
}
