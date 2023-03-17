/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data;

import org.openapitools.client.ApiClient;

/** This Class extends ApiClient Class for setting UserAgent */
public class ApiClientUserAgent extends ApiClient {

  Setup setup = new Setup();

  public ApiClientUserAgent() {
    setUserAgent(
        String.format(setup.getPACKAGE_ID())
            .concat(
                setup.getPACKAGE_VERSION()
                    + " (Java "
                    + setup.getJAVA_VERSION()
                    + ";"
                    + setup.getOS_NAME()
                    + ";arch "
                    + setup.getARCH()
                    + ")"));
  }

  public ApiClientUserAgent(String userAgentSuffix) {
    setUserAgent(
        String.format(setup.getPACKAGE_ID())
            .concat(
                setup.getPACKAGE_VERSION()
                    + " (Java "
                    + setup.getJAVA_VERSION()
                    + ";"
                    + setup.getOS_NAME()
                    + ";arch "
                    + setup.getARCH()
                    + ")"
                    + userAgentSuffix));
  }

  /**
   * This Method is overriden from ApiClient.
   *
   * @param userAgent HTTP request's user agent
   * @return
   */
  @Override
  public ApiClient setUserAgent(String userAgent) {
    addDefaultHeader("User-Agent", userAgent);
    return this;
  }
}
