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

While Logs Ingestion we will be passing either single request or batching request, "Batching is Bluk of request passed in single API call". 
To determine if the request is batching request or single request, we have boolean variable as "batch" which can be true or false accordingly by default batch is set as true.

We also have Gzip functionality where the data will be send in the compressed form.The gzip format is a technique used to speed up the sending of data over the internet .Gzip compression is used increase the throughput of data. we have boolean variable as "gzip" which can be true or false accordingly by default, Gzip is set as true.

We have also implemented rate limit in Data-SDK where "requestPerMin" is integer variable which is used to set maximum number of requests to be invoke per min, by default it's set to 100. This helps to avoid the data loss from new request above maximum limit.

There is a size-based rate limiting feature that limits the payload with 8 MB of data, and maximum array size for single log message is 32 KB if the limit of log message exceeds we will be logging it as a warning to the user as "Your message exceeds 32KB It will be truncate"

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


