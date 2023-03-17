/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.openapitools.client.ApiClient;

public class TestApiClientUserAgent {

  ApiClientUserAgent apiClient = new ApiClientUserAgent();

  Setup setup = new Setup();

  String userAgent = String.format(setup.getPACKAGE_ID()).concat(" " + setup.getPACKAGE_VERSION());

  @Test
  public void testUserAgent() {
    ApiClient apiClientUserAgent = apiClient.setUserAgent(userAgent);
    Assertions.assertEquals(apiClient, apiClientUserAgent);
  }

  @Test
  public void testUserAgentWithSuffix() {
    ApiClientUserAgent apiClient = new ApiClientUserAgent("test-application");
    ApiClient apiClientUserAgent = apiClient.setUserAgent(userAgent);
    Assertions.assertEquals(apiClient, apiClientUserAgent);
  }

  @Test
  public void testUserAgentWithSuffixGreaterThan32Char() {
    ApiClientUserAgent apiClient =
        new ApiClientUserAgent("test-application-application-name-32char");
    ApiClient apiClientUserAgent = apiClient.setUserAgent(userAgent);
    Assertions.assertEquals(apiClient, apiClientUserAgent);
  }
}
