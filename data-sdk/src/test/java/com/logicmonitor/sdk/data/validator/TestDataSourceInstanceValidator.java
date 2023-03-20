/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.validator;

import com.logicmonitor.sdk.data.model.DataSourceInstance;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class TestDataSourceInstanceValidator {

  DataSourceInstanceValidator instanceValidator = new DataSourceInstanceValidator();

  @Test
  public void testValidator() {
    String expected = "Either Instance Name or Instance Id is Mandatory.";
    final DataSourceInstance dataSourceInstance = DataSourceInstance.builder().build();
    Assertions.assertEquals(expected, instanceValidator.validator(dataSourceInstance));
  }

  @Test
  public void providedSpacesInInstanceName() {
    String expected = "Instance Name Should not be empty or have tailing spaces.";
    Assertions.assertEquals(
        expected, instanceValidator.checkInstanceNameValidation(" dataSourceInstanceName"));
  }

  @Test
  public void testIsValidInstanceName() {
    Assertions.assertEquals(
        false, instanceValidator.isValidInstanceName("data@Source Instance Name"));
  }

  @Test
  public void providedEmptyInstanceDisplayName() {
    String expected = "Instance display name Should not be empty or have tailing spaces.";
    Assertions.assertEquals(expected, instanceValidator.checkInstanceDisplayNameValidation(""));
  }

  @Test
  public void testValidateDeviceDisplayName() {
    String expected = "Space is not allowed at start and end in instance display name.";
    Assertions.assertEquals(
        expected, String.valueOf(instanceValidator.validateDeviceDisplayName(" displayName ")));
  }

  @Test
  public void providedEmptyInstancePropertiesKey() {
    String expected = "Instance Properties Key should not be null, empty or have trailing spaces.";
    Map<String, String> instanceProperties = new HashMap<>();
    instanceProperties.put("", "instancePropertyValue");
    Assertions.assertEquals(
        expected, instanceValidator.checkInstancePropertiesValidation(instanceProperties));
  }

  @Test
  public void providedEmptyInstancePropertiesValue() {
    String expected =
        "Instance Properties Value should not be null, empty or have trailing spaces.";
    Map<String, String> instanceProperties = new HashMap<>();
    instanceProperties.put("instancePropertyKey", "");
    Assertions.assertEquals(
        expected, instanceValidator.checkInstancePropertiesValidation(instanceProperties));
  }

  @Test
  public void providedInvalidInstancePropertiesKey() {
    String expected = "Invalid instance Properties key : instance@Property Key.";
    Map<String, String> instanceProperties = new HashMap<>();
    instanceProperties.put("instance@Property Key", "instancePropertyValue");
    Assertions.assertEquals(
        expected, instanceValidator.checkInstancePropertiesValidation(instanceProperties));
  }

  @Test
  public void providedSystemProperties() {
    String expected =
        "Instance Properties Should not contain System or auto properties : system.displayname.";
    Map<String, String> instanceProperties = new HashMap<>();
    instanceProperties.put("system.displayname", "testing-Properties");
    Assertions.assertEquals(
        expected, instanceValidator.checkInstancePropertiesValidation(instanceProperties));
  }

  @Test
  public void providedInvalidInstancePropertiesValues() {
    String expected =
        "Invalid instance properties value : 1!@#$%^&*() for Key : instancePropertyKey.";
    Map<String, String> instanceProperties = new HashMap<>();
    instanceProperties.put("instancePropertyKey", "1!@#$%^&*()");
    Assertions.assertEquals(
        expected, instanceValidator.checkInstancePropertiesValidation(instanceProperties));
  }

  @Test
  public void testPassEmptyAndSpellCheck() {
    Assertions.assertEquals(true, instanceValidator.passEmptyAndSpellCheck(" DataSourceInstance"));
  }

  @Test
  public void testIsInValidId() {
    Assertions.assertEquals(true, instanceValidator.isInValidId("?DataSourceInstance"));
  }

  @Test
  public void testIsNameLengthValid() {
    Assertions.assertEquals(
        false, instanceValidator.isNameLengthValid("Data Source Instance Name"));
  }

  @Test
  public void testIsValidInstanceID9Digit() {
    Assertions.assertEquals(true, instanceValidator.isValidInstanceID9Digit(123));
  }

  @Test
  public void testIsValidInstanceIdExpo() {
    Assertions.assertEquals(false, instanceValidator.isValidInstanceIdExpo(123));
  }

  @Test
  public void testCheckInstanceId() {
    String errorMsg = instanceValidator.checkInstanceId(1234567810);
    Assertions.assertEquals("Instance Id cannot be more than 9 digit.", errorMsg);
  }

  @Test
  public void testValidatorInstanceId() {
    final DataSourceInstance dataSourceInstance = DataSourceInstance.builder().id(123).build();
    Assertions.assertEquals("", instanceValidator.validator(dataSourceInstance));
  }

  @Test
  public void testValidatorDisplayName() {
    final DataSourceInstance dataSourceInstance =
        DataSourceInstance.builder().id(123).displayName("test").build();
    Assertions.assertEquals("", instanceValidator.validator(dataSourceInstance));
  }

  @Test
  public void testValidatorName() {
    final DataSourceInstance dataSourceInstance =
        DataSourceInstance.builder().name("instanceValidator").build();
    Assertions.assertEquals("", instanceValidator.validator(dataSourceInstance));
  }

  @Test(expected = Exception.class)
  public void testInvalidDisplayName() {
    final DataSourceInstance dataSourceInstance = DataSourceInstance.builder().build();
    StringBuilder errorMsg =
        instanceValidator.validateDeviceDisplayName(dataSourceInstance.getName());
    Assertions.assertEquals(errorMsg, "Instance display name can't be empty.");
  }

  @Test
  public void testDatasourceInstaneBuilder() {
    HashMap<String, String> datasourceInstanceProperty = new HashMap<>();
    String errorMsg =
        "Instance Properties Should not contain System or auto properties : system.property.";
    datasourceInstanceProperty.put("system.property", "Instance-property");
    final DataSourceInstance dataSourceInstance =
        DataSourceInstance.builder()
            .name("instanceValidator")
            .description("DatasourceInstance")
            .properties(datasourceInstanceProperty)
            .build();
    Assertions.assertEquals(errorMsg, instanceValidator.validator(dataSourceInstance));
  }
}
