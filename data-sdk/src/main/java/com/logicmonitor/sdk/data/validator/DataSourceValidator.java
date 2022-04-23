/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.validator;

import com.logicmonitor.sdk.data.model.DataSource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** This Class is used check validation for DataSource . */
@Builder
@NoArgsConstructor
public class DataSourceValidator implements AttributesValidator<DataSource> {

  private static final String REGEX_INVALID_DATA_SOURCE_NAME = "[^a-zA-Z $#@_0-9:&\\.\\+\n]";
  private static final String REGEX_INVALID_DATA_SOURCE_DISPLAY_NAME =
      "[^a-zA-Z: _0-9\\(\\)\\.#\\+@<>\n]";
  private static final String REGEX_DATA_SOURCE_GROUP_NAME = "[a-zA-Z0-9_\\- ]+$";
  private static final String REGEX_DATA_SOURCE_ID_9_DIGIT = "^[0-9]{0,9}$";
  private static final String REGEX_DATA_SOURCE_ID_EXPO = "^e\\^\\-?\\d*?$";

  private final Pattern patternInvalidDataSourceName =
      Pattern.compile(REGEX_INVALID_DATA_SOURCE_NAME);
  private final Pattern patternInvalidDataSourceDisplayName =
      Pattern.compile(REGEX_INVALID_DATA_SOURCE_DISPLAY_NAME);
  private final Pattern patternDataSourceGroupName = Pattern.compile(REGEX_DATA_SOURCE_GROUP_NAME);
  private final Pattern patternDataSourceId9Digit = Pattern.compile(REGEX_DATA_SOURCE_ID_9_DIGIT);
  private final Pattern patternDataSourceIdExpo = Pattern.compile(REGEX_DATA_SOURCE_ID_EXPO);

  @Override
  public String validator(DataSource dataSource) {
    Integer dataSourceId = dataSource.getId();
    String name = dataSource.getName();
    String displayName = dataSource.getDisplayName();
    String group = dataSource.getGroup();
    String errorMsg = "";

    if (dataSourceId != null) {
      if (dataSourceId >= 0) {
        errorMsg += checkDataSourceId(dataSourceId);
      } else {
        errorMsg += String.format("DataSource Id %d should not be negative.", dataSourceId);
      }
    } else {
      if (name == null) {
        errorMsg += "Either dataSourceId or dataSource is mandatory.";
      }
    }
    if (name != null) {
      errorMsg += checkDataSourceNameValidation(name);
    }
    errorMsg += checkDataSourceDisplayNameValidation(displayName);
    errorMsg += checkDataSourceGroupNameValidation(group);
    return errorMsg;
  }

  /**
   * This Method is used to check validation for DataSource Ids.
   *
   * @param dataSourceId
   * @return
   */
  protected String checkDataSourceId(int dataSourceId) {
    StringBuilder errorMsg = new StringBuilder();

    if (!isValidDataSourceId9Digit(dataSourceId)) {
      errorMsg.append("DataSource Id cannot be more than 9 digit.");
    }
    if (isValidDataSourceIdExpo(dataSourceId)) {
      errorMsg.append("DataSource Id cannot be in Exponential form.");
    }
    return errorMsg.toString();
  }

  /**
   * This Method is used to check validation for DataSource Name.
   *
   * @param dataSource
   * @return
   */
  public String checkDataSourceNameValidation(String dataSource) {
    StringBuilder errorMsg = new StringBuilder();

    if (passEmptyAndSpellCheck(dataSource)) {
      errorMsg.append("Datasource Name Should not be empty or have tailing spaces.");
    } else if (dataSource.length() > 64) {
      errorMsg.append("Datasource Name size should not be greater than 64 characters.");
    } else {
      String invalidName = validateDataSourceName(dataSource);

      if (invalidName != null) {
        errorMsg.append(invalidName);
      }
    }
    return errorMsg.toString();
  }

  /**
   * This Method is used to check validation for DataSource DisplayName.
   *
   * @param dataSourceDisplayName
   * @return
   */
  protected String checkDataSourceDisplayNameValidation(String dataSourceDisplayName) {
    StringBuilder errorMsg = new StringBuilder();

    if (dataSourceDisplayName != null) {
      if (passEmptyAndSpellCheck(dataSourceDisplayName)) {
        errorMsg.append("Datasource display name Should not be empty or have tailing spaces.");
      } else if (dataSourceDisplayName.length() > 64) {
        errorMsg.append("Datasource display name size should not be greater than 64 characters.");
      } else {
        String invalidName = validateDataSourceDisplayName(dataSourceDisplayName);
        if (invalidName != null) {
          errorMsg.append(invalidName);
        }
      }
    }
    return errorMsg.toString();
  }

  /**
   * This Method is used to check validation for DataSource Name.
   *
   * @param dataSourceGroupName
   * @return
   */
  protected String checkDataSourceGroupNameValidation(String dataSourceGroupName) {
    StringBuilder errorMsg = new StringBuilder();

    if (dataSourceGroupName != null) {
      if (passEmptyAndSpellCheck(dataSourceGroupName)) {
        errorMsg.append("Datasource group name Should not be empty or have tailing spaces.");
      } else if (dataSourceGroupName.length() < 2) {
        errorMsg.append("Datasource group name size should not be less than 2 characters.");
      } else if (dataSourceGroupName.length() > 128) {
        errorMsg.append("Datasource group name size should not be greater than 128 characters.");
      } else if (!isValidDataSourceGroupName(dataSourceGroupName)) {
        errorMsg.append(String.format("Invalid datasource group name : %s.", dataSourceGroupName));
      }
    }
    return errorMsg.toString();
  }

  protected boolean isValidDataSourceId9Digit(int dataSourceID) {
    return patternDataSourceId9Digit.matcher(String.valueOf(dataSourceID)).matches();
  }

  protected boolean isValidDataSourceIdExpo(int dataSourceID) {
    return patternDataSourceIdExpo.matcher(String.valueOf(dataSourceID)).matches();
  }

  protected boolean passEmptyAndSpellCheck(String name) {
    return name.length() == 0 || name.startsWith(" ") || name.endsWith(" ");
  }

  protected boolean isValidDataSourceGroupName(String name) {
    return patternDataSourceGroupName.matcher(name).matches();
  }

  protected String validateDataSourceDisplayName(String name) {
    if (name.length() == 0) {
      return "Datasource display name cannot be empty.";
    }
    if (name.contains("-")) {
      if (name.indexOf("-") == name.length() - 1) {
        if (name.length() == 1) {
          return "Datasource display name cannot be single \"-\".";
        }
        name = name.replaceAll(" *-$", "");
        if (name.length() == 0) {
          return "space is not allowed in start and end.";
        }
      } else {
        return "Support \"-\" for datasource display name when its the last char.";
      }
    }
    return validate(name, "Datasource display name", patternInvalidDataSourceDisplayName);
  }

  protected String validateDataSourceName(String name) {
    if (name.length() == 0) {
      return "Datasource name cannot be empty.";
    }
    if (name.contains("-")) {
      if (name.indexOf("-") == (name.length() - 1)) {
        if (name.length() == 1) {
          return "Datasource name cannot be single \"-\".";
        }
        name = name.replaceAll(" *-$", "");
        if (name.length() == 0) {
          return "Space is not allowed in start and end.";
        }
      } else {
        return "Support \"-\" for datasource name when its the last char.";
      }
    }
    return validate(name, "Datasource name", patternInvalidDataSourceName);
  }

  protected String validate(String name, String fieldName, Pattern patternInvalidDataSourceName) {
    String test = "##";
    if (name == null) {
      return String.format("%s can't be null.", fieldName);
    }
    if (name.startsWith(" ") || name.endsWith(" ")) {
      return String.format("Space is not allowed at start and end in %s.", fieldName);
    }
    Matcher matcher = patternInvalidDataSourceName.matcher(name);
    if (matcher.find()) {
      return String.format("Invalid %s : %s.", fieldName, name);
    } else {
      if (name.contains(test)) {
        return String.format("Invalid %s : %s.", fieldName, name);
      }
    }
    return "";
  }
}
