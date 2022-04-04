/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

/** This Class is used to Set UserAgent */
@Getter
@NoArgsConstructor
public class Setup {

  private String PACKAGE_ID = "logicmonitor_data_sdk/";
  private String PACKAGE_VERSION = "0.0.1.alpha";
  private String JAVA_VERSION = System.getProperty("java.version");
  private String OS_NAME = System.getProperty("os.name");
  private String ARCH = System.getProperty("os.arch");
}
