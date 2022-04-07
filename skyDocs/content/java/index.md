# The LogicMonitor Java Data library

This Java Library is suitable for ingesting the metrics or logs into the LogicMonitor Platform.

## Overview

LogicMonitor's Push Metrics feature allows you to send metrics directly
to the LogicMonitor platform via a dedicated API, removing the need to
route the data through a LogicMonitor Collector. Once ingested, these
metrics are presented alongside all other metrics gathered via
LogicMonitor, providing a single pane of glass for metric monitoring and
alerting.

Similarly, If a log integration isn’t available or you have custom logs that you want to analyze,
you can send the logs directly to your LogicMonitor account via the logs ingestion API.

## Quick Start Notes:

### Use [GitHub Package](https://github.com/orgs/logicmonitor/packages?repo_name=lm-data-sdk-java)

Add the package dependencies to your build.gradle or pom.xml

#### Gradle

```groovy
dependencies {
    implementation('com.logicmonitor:lm-data-sdk:0.0.1-alpha')
}
```

#### Maven

```xml

<dependency>
  <groupId>com.logicmonitor</groupId>
  <artifactId>lm-data-sdk</artifactId>
  <version>0.0.1-alpha</version>
</dependency>
```
### Set Configurations
SDK must be configured with LogicMonitor.DataSDK Configuration class. While using LMv1 authentication set LM_ACCESS_ID and
LM_ACCESS_KEY properties, In Case of BearerToken Authentication set LM_BEARER_TOKEN property. Company's name or Account
name <b>must</b> be passed to LM_COMPANY property. All properties can be set using environment variable.

|      Environment variable      |  Description |
|-------------|:------|
|  `LM_COMPANY` |  Account name (Company Name) is your organization name |
|  `LM_ACCESS_ID` |  Access id while using LMv1 authentication.  |
|  `LM_ACCESS_KEY` |    Access key while using LMv1 authentication.|
|  `LM_BEARER_TOKEN` |    BearerToken while using Bearer authentication. |

### Read the [Metrics](https://logicmonitor.github.io/lm-data-sdk-java/java/MetricsIngestion.html) / [Logs](https://logicmonitor.github.io/lm-data-sdk-java/java/LogIngestion.html) example to use Metrics/Logs ingestion API.

### See the project repository at [GitHub](https://github.com/logicmonitor/lm-data-sdk-java).

## Get in Touch

If you have questions in general, reach out to our [support](https://support@logicmonitor.com)


------------
Copyright, 2022, LogicMonitor, Inc.

This Source Code Form is subject to the terms of the
Mozilla Public License, v. 2.0. If a copy of the MPL
was not distributed with this file, You can obtain
one at https://mozilla.org/MPL/2.0/.



