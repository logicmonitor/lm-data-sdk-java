/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.validator;

import com.logicmonitor.sdk.data.model.DataSource;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class TestDataSourceValidator {

  DataSourceValidator dataSourceValidator = new DataSourceValidator();

  @Test
  public void testValidator() {
    String expected = "Either dataSourceId or dataSource is mandatory.";
    final DataSource dataSource =
        DataSource.builder().group("Java Data SDK Group").singleInstanceDS(false).build();
    Assertions.assertEquals(expected, dataSourceValidator.validator(dataSource));
  }

  @Test
  public void providedEmptyDataSourceName() {
    String expected = "Datasource Name Should not be empty or have tailing spaces.";
    Assertions.assertEquals(
        expected, dataSourceValidator.checkDataSourceNameValidation(" DataSource-Name"));
  }

  @Test
  public void providedSingleDataSourceName() {
    String expected = "Datasource name cannot be single \"-\".";
    Assertions.assertEquals(expected, dataSourceValidator.validateDataSourceName("-"));
  }

  @Test
  public void providedSpacesInDataSourceName() {
    String expected = "Space is not allowed at start and end in Datasource name.";
    Assertions.assertEquals(
        expected, dataSourceValidator.validateDataSourceName(" dataSourceName-"));
  }

  @Test
  public void checkInvalidDataSourceName() {
    String expected = "Support \"-\" for datasource name when its the last char.";
    Assertions.assertEquals(
        expected, dataSourceValidator.validateDataSourceName("-dataSourceName"));
  }

  @Test
  public void testCheckDataSourceDisplayNameValidation() {
    String expected = "Datasource display name Should not be empty or have tailing spaces.";
    Assertions.assertEquals(
        expected, dataSourceValidator.checkDataSourceDisplayNameValidation(" DataSourceName"));
  }

  @Test
  public void providedEmptyDataSourceDisplayName() {
    String expected = "Datasource display name cannot be empty.";
    Assertions.assertEquals(expected, dataSourceValidator.validateDataSourceDisplayName(""));
  }

  @Test
  public void providedSingleDataSourceDisplayName() {
    String expected = "Datasource display name cannot be single \"-\".";
    Assertions.assertEquals(expected, dataSourceValidator.validateDataSourceDisplayName("-"));
  }

  @Test
  public void providedSpaceAtTheEndOfDataSourceDisplayName() {
    String expected = "Space is not allowed at start and end in Datasource display name.";
    Assertions.assertEquals(
        expected, dataSourceValidator.validateDataSourceDisplayName("Data Source Name "));
  }

  @Test
  public void testCheckDataSourceGroupNameValidation() {
    String expected = "Datasource group name Should not be empty or have tailing spaces.";
    Assertions.assertEquals(
        expected, dataSourceValidator.checkDataSourceGroupNameValidation(" Data-Source-Group"));
  }

  @Test
  public void testCheckDataSourceGroupSize() {
    String expected = "Datasource group name size should not be less than 2 characters.";
    Assertions.assertEquals(expected, dataSourceValidator.checkDataSourceGroupNameValidation("d"));
  }

  @Test
  public void testIsValidDataSourceGroupName() {
    Assertions.assertEquals(
        false, dataSourceValidator.isValidDataSourceGroupName("DataSource*Group"));
  }

  @Test
  public void testPassEmptyAndSpellCheck() {
    Assertions.assertEquals(true, dataSourceValidator.passEmptyAndSpellCheck(" DataSource"));
  }

  @Test
  public void validateDataSource() {
    DataSource dataSource =
        DataSource.builder()
            .name("Data_Source_name")
            .group("Data-Source-Group-name")
            .displayName("Data_Source_Display_name")
            .id(2345)
            .singleInstanceDS(false)
            .build();
    Assertions.assertEquals("", dataSourceValidator.validator(dataSource));
  }

  @Test
  public void testDataSourceNameLength() {
    String expected = "Datasource Name size should not be greater than 64 characters.";
    char[] chars = new char[65];
    String name = new String(chars);
    Assertions.assertEquals(expected, dataSourceValidator.checkDataSourceNameValidation(name));
  }

  @Test
  public void testDataSourceDisplayNameLength() {
    String expected = "Datasource display name size should not be greater than 64 characters.";
    char[] chars = new char[65];
    String name = new String(chars);
    Assertions.assertEquals(
        expected, dataSourceValidator.checkDataSourceDisplayNameValidation(name));
  }

  @Test
  public void testDataSourceGroupNameLength() {
    String expected = "Datasource group name size should not be greater than 128 characters.";
    char[] chars = new char[129];
    String name = new String(chars);
    Assertions.assertEquals(expected, dataSourceValidator.checkDataSourceGroupNameValidation(name));
  }

  @Test
  public void testDataSourceGroupNameValidation() {
    String expected = "Invalid datasource group name : Data@source-Group-Name.";
    Assertions.assertEquals(
        expected, dataSourceValidator.checkDataSourceGroupNameValidation("Data@source-Group-Name"));
  }

  @Test
  public void testDataSourceDisplayName() {
    String expected = "Support \"-\" for datasource display name when its the last char.";
    Assertions.assertEquals(
        expected, dataSourceValidator.validateDataSourceDisplayName("Data-source-display-name"));
  }

  @Test
  public void testDataSourceName() {
    String expected = "Datasource name cannot be empty.";
    Assertions.assertEquals(expected, dataSourceValidator.validateDataSourceName(""));
  }

  @Test
  public void testDataSourceNameValidation() {
    String expected = "Invalid Datasource name : data##SourceName.";
    Assertions.assertEquals(
        expected, dataSourceValidator.validateDataSourceName("data##SourceName"));
  }

  @Test
  public void testValidateDataSourceName() {
    String expected = "Invalid Datasource name : data'SourceName.";
    Assertions.assertEquals(
        expected, dataSourceValidator.validateDataSourceName("data'SourceName"));
  }
}
