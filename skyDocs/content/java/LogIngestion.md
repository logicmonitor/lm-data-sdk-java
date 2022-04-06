# LogicMonitor.DataSDK - the Java library for the LogicMonitor Logs ingest API

LogicMonitor is a SaaS-based performance monitoring platform that provides full visibility into
complex, hybrid infrastructures, offering granular performance monitoring and actionable data and
insights. If you have custom logs that you want to analyze, you can send the logs directly to your LogicMonitor account via the logs ingestion API. 
For using this application users have to create LMAuth token to get access id and
key from santaba.

- SDK version: 0.0.1-alpha

<a name = "Single Log Ingestion"></a>

## Single Log Ingestion

SDK must be configured with LogicMonitor.DataSDK Configuration class. While using LMv1 authentication set LM_ACCESS_ID and
LM_ACCESS_KEY properties. Company's name or Account
name <b>must</b> be passed to LM_COMPANY property. All properties can be set using environment variable.

For Log ingestion, log message has to be passed along the resource object to identify the
resource. Create a resource object using LogicMonitor.DataSDK.Models namespace.



```java
Configuration conf = new Configuration();
final boolean batch = false;
HashMap<String, String> resourceIds = new HashMap<>();
resourceIds.put("system.displayname","data-logs");
Resource resource = Resource.builder().ids(resourceIds).build();
Logs logs = new Logs(conf, 10, batch, responseInterface);
logs.sendLogs("Testing log Api", resource, metadata);
```

While Logs Ingestion we will be passing either single request or batching request, "Batching is Bluk of request will be passed in single API call". 
To determine if user is sending bathing request or single request, we have boolean variable as "batch" which can be true or false accordingly.

<a name="Model"></a>

## Model

- ### Resource

```java
Resource resource = Resource.builder().ids(resourceIds).build();
```

  <b>Ids(Dictonary<string,string>):</b> 
  <br>
  An Dictionary of existing resource properties that will be
  used to identify the resource. See Managing Resources that Ingest Push Metrics for information on
  the types of properties that can be used. If no resource is matched and the create parameter is set
  to TRUE, a new resource is created with these specified resource IDs set on it. If the
  system.displayname and/or system.hostname property is included as resource IDs, they will be used as
  host name and display name respectively in the resulting resource.

- ### Message:
  Message is string which contains the message that is used to be logged and send to LM site.

- ### Metadata:
  Metadata is dictionary which can used to ingest metadata. It can be viewd on LM site along with the
  logs and can we logged message.

- ### Timestamp:
  Log generated time in Date Time format.


