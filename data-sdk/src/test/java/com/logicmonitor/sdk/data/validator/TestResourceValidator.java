/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.validator;

import com.logicmonitor.sdk.data.model.Resource;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class TestResourceValidator {

  ResourceValidator resourceValidator = new ResourceValidator();

  @Test
  public void testPassEmptyAndSpellCheck() {
    Assertions.assertEquals(true, resourceValidator.passEmptyAndSpellCheck(""));
  }

  @Test
  public void testIsValidResourceName() {
    Assertions.assertEquals(false, resourceValidator.isValidResourceName("Test@test"));
    Assertions.assertEquals(true, resourceValidator.isValidResourceName("Test-Resource-Name"));
  }

  @Test
  public void testIsNameLengthValid() {
    Assertions.assertEquals(false, resourceValidator.isNameLengthValid("Test-Resource-Name"));
  }

  @Test
  public void providedNullResourceName() {
    String expected = "Resource name is mandatory.";
    String actual = resourceValidator.checkResourceNameValidation(true, null);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void providedEmptyResourceName() {
    String expected = "Resource Name Should not be empty or have tailing spaces.";
    Assertions.assertEquals(expected, resourceValidator.checkResourceNameValidation(true, " "));
    Assertions.assertEquals(
        expected, resourceValidator.checkResourceNameValidation(true, " Test-Resource-Name"));
  }

  @Test
  public void testCheckResourceNamDescriptionValidation() {
    char[] chars = new char[65537];
    String description = new String(chars);
    String expected = "Resource Description Size should not be greater than 65535 characters.";
    Assertions.assertEquals(
        expected, resourceValidator.checkResourceNamDescriptionValidation(description));
  }

  @Test
  public void providedEmptyResourceId() {
    String expected = "No Element in Resource Id.";
    Map<String, String> resourceIds = new HashMap<>();
    Assertions.assertEquals(expected, resourceValidator.checkResourceIdsValidation(resourceIds));
  }

  @Test
  public void providedNullResourceIdKey() {
    String expected = "Resource Id Key should not be null, empty or have trailing spaces.";
    Map<String, String> resourceIds = new HashMap<>();
    resourceIds.put("", "Test-Resource-id");
    Assertions.assertEquals(expected, resourceValidator.checkResourceIdsValidation(resourceIds));
  }

  @Test
  public void providedNullResourceIdValue() {
    String expected = "Resource Id Value should not be null, empty or have trailing spaces.";
    Map<String, String> resourceIds = new HashMap<>();
    resourceIds.put("system.displayname", "");
    Assertions.assertEquals(expected, resourceValidator.checkResourceIdsValidation(resourceIds));
  }

  @Test
  public void testIsInValidResourceId() {
    Assertions.assertEquals(false, resourceValidator.isInValidResourceId("*Resource_Id"));
  }

  @Test
  public void providedEmptyResourceProperties() {
    String expected = "No Element in Resource Properties.";
    Map<String, String> resourceProperties = new HashMap<>();
    Assertions.assertEquals(
        expected, resourceValidator.checkResourcePropertiesValidation(resourceProperties));
  }

  @Test
  public void providedNullResourcePropertiesKey() {
    String expected = "Resource Properties Key should not be null, empty or have trailing spaces.";
    Map<String, String> resourceProperties = new HashMap<>();
    resourceProperties.put("", "Test-Resource-Property");
    Assertions.assertEquals(
        expected, resourceValidator.checkResourcePropertiesValidation(resourceProperties));
  }

  @Test
  public void providedInvalidResourcePropertiesKey() {
    String expected = "Invalid resource Properties key : *Resource_Id.";
    Map<String, String> resourceProperties = new HashMap<>();
    resourceProperties.put("*Resource_Id", "Testing-Properties ");
    Assertions.assertEquals(
        expected, resourceValidator.checkResourcePropertiesValidation(resourceProperties));
  }

  @Test
  public void providedSystemProperties() {
    String expected =
        "Resource Properties Should not contain System or auto properties : system.displayname.";
    Map<String, String> resourceProperties = new HashMap<>();
    resourceProperties.put("system.displayname", "Testing-Properties");
    Assertions.assertEquals(
        expected, resourceValidator.checkResourcePropertiesValidation(resourceProperties));
  }

  @Test
  public void providedWrongPropertyName() {
    String expected = "Cannot use '##' in property name.";
    Map<String, String> resourceProperties = new HashMap<>();
    resourceProperties.put("##displayname", "Testing-Properties");
    Assertions.assertEquals(
        expected, resourceValidator.checkResourcePropertiesValidation(resourceProperties));
  }

  @Test
  public void testValidResourceName() {
    String expected = "Invalid resource name : Test@test.";
    Assertions.assertEquals(
        expected, resourceValidator.checkResourceNameValidation(true, "Test@test"));
  }

  @Test
  public void testResourceNameLength() {
    char[] chars = new char[257];
    String name = new String(chars);
    String expected = "Resource Name size should not be greater than 255 characters.";
    Assertions.assertEquals(expected, resourceValidator.checkResourceNameValidation(true, name));
  }

  @Test
  public void testResourceIdKeyLength() {
    char[] chars = new char[257];
    String id = new String(chars);
    String expected = "Resource Id Key should not be greater than 255 characters.";
    Map<String, String> resourceIds = new HashMap<>();
    resourceIds.put(id, "value");
    Assertions.assertEquals(expected, resourceValidator.checkResourceIdsValidation(resourceIds));
  }

  @Test
  public void testResourceIdKeyValidation() {

    String expected = "Invalid resource Id key : Resource@Id-Key.";
    Map<String, String> resourceIds = new HashMap<>();
    resourceIds.put("Resource@Id-Key", "value");
    Assertions.assertEquals(expected, resourceValidator.checkResourceIdsValidation(resourceIds));
  }

  @Test
  public void testResourceIdValueLength() {
    char[] chars = new char[24001];
    String id = new String(chars);
    String expected = "Resource Id Value should not be greater than 24000 characters.";
    Map<String, String> resourceIds = new HashMap<>();
    resourceIds.put("Resource-Id", id);
    Assertions.assertEquals(expected, resourceValidator.checkResourceIdsValidation(resourceIds));
  }

  @Test
  public void testResourceId() {
    String expected = "Invalid resource Id Value : Resource@Id#Value for Key : Resource-Id-Key.";
    Map<String, String> resourceIds = new HashMap<>();
    resourceIds.put("Resource-Id-Key", "Resource@Id#Value");
    Assertions.assertEquals(expected, resourceValidator.checkResourceIdsValidation(resourceIds));
  }

  @Test
  public void testResourcePropertyKeyLength() {
    char[] chars = new char[257];
    String property = new String(chars);
    String expected = "Resource Properties Key should not be greater than 255 characters.";
    Map<String, String> resourceProperty = new HashMap<>();
    resourceProperty.put(property, "value");
    Assertions.assertEquals(
        expected, resourceValidator.checkResourcePropertiesValidation(resourceProperty));
  }

  @Test
  public void testResourceProperties() {
    String expected =
        "Resource Properties Value should not be null, empty or have trailing spaces.";
    Map<String, String> resourceProperty = new HashMap<>();
    resourceProperty.put("Resource-property", "");
    Assertions.assertEquals(
        expected, resourceValidator.checkResourcePropertiesValidation(resourceProperty));
  }

  @Test
  public void testResourcePropertyValueLength() {
    char[] chars = new char[24001];
    String property = new String(chars);
    String expected = "Resource Properties Value should not be greater than 24000 characters.";
    Map<String, String> resourceProperty = new HashMap<>();
    resourceProperty.put("Resource-property", property);
    Assertions.assertEquals(
        expected, resourceValidator.checkResourcePropertiesValidation(resourceProperty));
  }

  @Test
  public void testResourceProperty() {
    String expected =
        "Invalid resource Properties Value : Resource@Property'Value for Key : Resource-property.";
    Map<String, String> resourceProperty = new HashMap<>();
    resourceProperty.put("Resource-property", "Resource@Property'Value");
    Assertions.assertEquals(
        expected, resourceValidator.checkResourcePropertiesValidation(resourceProperty));
  }

  @Test
  public void ValidateResources() {
    Resource testResource = new Resource();
    HashMap<String, String> resourceIds = new HashMap<>();
    HashMap<String, String> resourceProperty = new HashMap<>();
    resourceIds.put("system.displayname", "Resource-display-name");
    resourceProperty.put("Resource-property", "Resource-Property-Value");
    testResource.setName("Resource-Name");
    testResource.setProperties(resourceProperty);
    testResource.setCreate(true);
    testResource.setIds(resourceIds);
    testResource.setDescription("Resource-Description");

    Resource resource =
        Resource.builder()
            .ids(testResource.getIds())
            .name(testResource.getName())
            .description(testResource.getDescription())
            .properties(testResource.getProperties())
            .create(testResource.isCreate())
            .build();
    Assertions.assertEquals("", resourceValidator.validator(resource));
  }
}
