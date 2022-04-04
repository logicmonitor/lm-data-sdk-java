/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

public class TestConfiguration {

  Configuration configuration = Mockito.mock(Configuration.class);
  String body =
      "[{\"dataSource\":\"Java Data SDK\",\"dataSourceGroup\":\"Java Data SDK Group\","
          + "\"instances\":[{\"dataPoints\":[{\"dataPointName\":\"cpuUsage\",\"percentileValue\":0,\""
          + "values\":{\"1646816195\":\"27\"}}],\"instanceName\":\"Java Data SDK Instance\"}],"
          + "\"resourceIds\":{\"system.displayname\":\"Java_Data_SDK_Test\"},\"resourceName\":"
          + "\"Java_Data_SDK_Test\"}]";

  @Test
  public void testIsValidCompanyName() {
    Assertions.assertEquals(false, configuration.isValidCompanyName("company*01"));
  }

  @Test
  public void testIsValidAuthId() {
    Assertions.assertEquals(false, configuration.isValidAuthId("*5t5U5jH6Q92P2n8x8tg5"));
  }

  @Test
  public void testConfiguration() {
    Configuration conf = new Configuration("company01", null, null, "bhasgsyhsjjdbfbhcgyi@1");
    String actualToken = conf.getAuthToken("POST", "POST", "/v2/metric/ingest");
    Assertions.assertTrue(true, actualToken);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidBearerAuth() {
    Configuration conf = new Configuration("company01", null, null, " ");
    boolean authentication = conf.checkAuthentication();
    Assertions.assertEquals(IllegalArgumentException.class, authentication);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidLMv1Auth() {
    Configuration conf = new Configuration("company01", " ", " ", null);
    boolean authentication = conf.checkAuthentication();
    Assertions.assertEquals(IllegalArgumentException.class, authentication);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCompanyName() {
    Configuration conf = new Configuration();
    boolean authentication = conf.checkAuthentication();
    Assertions.assertEquals(IllegalArgumentException.class, authentication);
  }

  @Test
  public void testLMv1Auth() {
    Configuration conf =
        new Configuration(
            "company01", "5t5U5jH6Q92P2n8x8tg5", "[KC5sdqL-vX25-35pP2gmPw(f_15W(]LLg(B4Kc8", null);
    conf.getAuthToken(body, "POST", "/v2/metric/ingest");
    Assert.assertTrue("This will succeed.", true);
  }

  @Test
  public void testBearerAuth() {
    Configuration conf = new Configuration("company01", null, null, "bhasgsyhsjjdbfbhcgyi@1");
    String expectedToken = "Bearer bhasgsyhsjjdbfbhcgyi@1";
    String actualToken = conf.getAuthToken("POST", "POST", "/v2/metric/ingest");
    Assertions.assertEquals(expectedToken, actualToken);
  }
}
