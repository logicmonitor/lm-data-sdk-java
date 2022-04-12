/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.validator;

import com.logicmonitor.sdk.data.model.Resource;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** This Class is used check validation for Resource . */
@Builder
@NoArgsConstructor
public class ResourceValidator implements AttributesValidator<Resource> {

  private static final String REGEX_RESOURCE_NAME = "^[a-z:A-Z0-9\\._\\-]+$";

  private final Pattern patternResourceName = Pattern.compile(REGEX_RESOURCE_NAME);

  @Override
  public String validator(Resource resource) {
    String errorMsg = "";
    if (resource.getName() != null) {
      errorMsg += checkResourceNameValidation(resource.isCreate(), resource.getName());
    }
    if (resource.getDescription() != null) {
      errorMsg += checkResourceNamDescriptionValidation(resource.getDescription());
    }
    errorMsg += checkResourceIdsValidation(resource.getIds());
    if (resource.getProperties() != null) {
      errorMsg += checkResourcePropertiesValidation(resource.getProperties());
    }
    return errorMsg;
  }

  /**
   * This Method is used to check validation for resource name.
   *
   * @param createFlag This attribute is decide whether to create or not the resource
   * @param resourceName This is Resource Name
   * @return
   */
  protected String checkResourceNameValidation(Boolean createFlag, String resourceName) {
    StringBuilder errorMsg = new StringBuilder();

    if ((createFlag != null && createFlag) || resourceName == null) {
      if (resourceName == null) {
        errorMsg.append("Resource name is mandatory.");
      } else if (passEmptyAndSpellCheck(resourceName)) {
        errorMsg.append("Resource Name Should not be empty or have tailing spaces.");
      } else if (isNameLengthValid(resourceName)) {
        errorMsg.append("Resource Name size should not be greater than 255 characters.");
      } else if (!isValidResourceName(resourceName)) {
        errorMsg.append(String.format("Invalid resource name : %s.", resourceName));
      }
    }
    return errorMsg.toString();
  }

  /**
   * This Method is used to check validation for resource description.
   *
   * @param description This attribute us to set Resource Description
   * @return
   */
  protected String checkResourceNamDescriptionValidation(String description) {
    StringBuilder errorMsg = new StringBuilder();

    if (description != null && (description.length() > 65535)) {
      errorMsg.append("Resource Description Size should not be greater than 65535 characters.");
    }
    return errorMsg.toString();
  }

  /**
   * This Method is used to check validation for resource Ids.
   *
   * @param resourceIds This attribute us to set Resource Id
   * @return
   */
  public String checkResourceIdsValidation(Map<String, String> resourceIds) {
    StringBuilder errorMsg = new StringBuilder();

    if (resourceIds.size() == 0) {
      errorMsg.append(("No Element in Resource Id."));
    } else {
      for (Map.Entry<String, String> item : resourceIds.entrySet()) {
        if (passEmptyAndSpellCheck(item.getKey())) {
          errorMsg.append("Resource Id Key should not be null, empty or have trailing spaces.");
        } else if (isNameLengthValid(item.getKey())) {
          errorMsg.append("Resource Id Key should not be greater than 255 characters.");
        } else if (!isInValidResourceId(item.getKey())) {
          errorMsg.append(String.format("Invalid resource Id key : %s.", item.getKey()));
        } else if (passEmptyAndSpellCheck(item.getValue())) {
          errorMsg.append("Resource Id Value should not be null, empty or have trailing spaces.");
        } else if (item.getValue().length() > 24000) {
          errorMsg.append("Resource Id Value should not be greater than 24000 characters.");
        } else if (!isInValidResourceId(item.getValue())) {
          errorMsg.append(
              String.format(
                  "Invalid resource Id Value : %s for Key : %s.", item.getValue(), item.getKey()));
        }
      }
    }
    return errorMsg.toString();
  }

  /**
   * This Method is used to check validation for resource Properties.
   *
   * @param resourceProperties This attribute us to set Resource Properties
   * @return
   */
  public String checkResourcePropertiesValidation(Map<String, String> resourceProperties) {
    StringBuilder errorMsg = new StringBuilder();

    if (resourceProperties.size() == 0) {
      errorMsg.append("No Element in Resource Properties.");
    } else {
      for (Map.Entry<String, String> item : resourceProperties.entrySet()) {
        if (passEmptyAndSpellCheck(item.getKey())) {
          errorMsg.append(
              "Resource Properties Key should not be null, empty or have trailing spaces.");
        } else if (isNameLengthValid(item.getKey())) {
          errorMsg.append("Resource Properties Key should not be greater than 255 characters.");
        } else if (item.getKey().contains("##")) {
          errorMsg.append("Cannot use '##' in property name.");
        } else if (item.getKey().toLowerCase().startsWith("system.")
            || item.getKey().toLowerCase().startsWith("auto.")) {
          errorMsg.append(
              String.format(
                  "Resource Properties Should not contain System or auto properties : %s.",
                  item.getKey()));
        } else if (!isInValidResourceId(item.getKey())) {
          errorMsg.append(String.format("Invalid resource Properties key : %s.", item.getKey()));
        } else if (passEmptyAndSpellCheck(item.getValue())) {
          errorMsg.append(
              "Resource Properties Value should not be null, empty or have trailing spaces.");
        } else if (item.getValue().length() > 24000) {
          errorMsg.append("Resource Properties Value should not be greater than 24000 characters.");
        } else if (!isInValidResourceId(item.getValue())) {
          errorMsg.append(
              String.format(
                  "Invalid resource Properties Value : %s for Key : %s.",
                  item.getValue(), item.getKey()));
        }
      }
    }
    return errorMsg.toString();
  }

  /**
   * @param name
   * @return
   */
  protected boolean passEmptyAndSpellCheck(String name) {
    return name.length() == 0 || name.startsWith(" ") || name.endsWith(" ");
  }

  /**
   * @param name
   * @return
   */
  protected boolean isNameLengthValid(String name) {
    return name.length() > 255;
  }

  /**
   * @param resourceName This attribute used to validate Resource Name
   * @return
   */
  protected boolean isValidResourceName(String resourceName) {
    return patternResourceName.matcher(resourceName).matches();
  }

  /**
   * @param resourceId This attribute used to validate Resource Id
   * @return
   */
  protected boolean isInValidResourceId(String resourceId) {
    return patternResourceName.matcher(resourceId).matches();
  }
}
