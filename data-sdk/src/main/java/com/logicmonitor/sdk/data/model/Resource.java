/*
 * Copyright, 2022, LogicMonitor, Inc.
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */
package com.logicmonitor.sdk.data.model;

import java.util.Map;
import lombok.*;

/** This Model is used to identify a resource. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

  private Map<String, String> ids;

  private String name;

  private String description;

  private Map<String, String> properties;

  private boolean create;
}
