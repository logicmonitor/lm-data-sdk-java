package com.logicmonitor.sdk.data.model;

import java.util.Map;
import lombok.Setter;

@Setter
public class RestMetrics {

  private Map<String, String> resourceIds;

  private String dataSource;

  private String dataSourceDisplayName;

  private String instanceName;

  private Map<String, String> instanceProperties;
}
