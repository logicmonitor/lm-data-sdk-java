# LogicMonitor.DataSDK - the Java library for the LogicMonitor Metrics ingest API

LogicMonitor is a SaaS-based performance monitoring platform that provides full visibility into
complex, hybrid infrastructures, offering granular performance monitoring and actionable data and
insights. Metrics-Ingest provides the entry point in the form of public rest APIs for ingesting metrics
into LogicMonitor. For using this application users have to create LMAuth token to get access id and
key or create Bearer token from santaba.

- SDK version: 0.0.1-alpha

<a name = "Metrics Ingestion Example"></a>

## Metrics Ingestion Example

SDK must be configured with LogicMonitor.DataSDK Configuration class. While using LMv1 authentication set LM_ACCESS_ID and
LM_ACCESS_KEY properties, In Case of BearerToken Authentication set LM_BEARER_TOKEN property. Company's name or Account
name <b>must</b> be passed to LM_COMPANY property. All properties can be set using environment variable.

For metrics ingestion user must create a object of Resource, DataSource, DataSourceInstance and
DataPoint using LogicMonitor.DataSDK.Model namespace, also Map should be created in which '
Key' hold the Time(in epoch) for which data is being emitted and 'Value' will the value of
datapoint.


```java
//Pass autheticate variable as Environment variable.
Configuration conf = new Configuration();
final boolean batch = false;
Metrics metrics = new Metrics(conf, 10, batch, responseInterface);

Resource resource = Resource.builder().ids(resourceIds).name(resourceName).build();
DataSource dataSource = DataSource.builder().name(dataSourceName).group(dataSourceGroup).singleInstanceDS(false).build();
DataSourceInstance dataSourceInstance = DataSourceInstance.builder().name(instanceName).build();
DataPoint dataPoint = DataPoint.builder().name(cpuUsage).build();
Map<String, String> cpuUsageValue = new HashMap<>();

cpuUsageValue.put(String.valueOf(Instant.now().getEpochSecond()), cpuUsageMetric);
metrics.sendMetrics(resource, dataSource, dataSourceInstance, dataPoint2, cpuUsageValue);
```

While Metrics Ingestion we will be passing either single request or batching request, "Batching is Bluk of request will be passed in single API call".
To determine if user is sending bathing request or single request, we have boolean variable as "batch" which can be true or false accordingly.

Read below for understanding more about Models in SDK.

<a name="Model"></a>

## Model

- ### Resource

```java
Resource resource = new Resource(ids, name, description, properties, create);
```

<b>Ids(Dictonary<String, String>):</b> <br>An Dictionary of existing resource properties that will be
used to identify the resource. See Managing Resources that Ingest Push Metrics for information on
the types of properties that can be used. If no resource is matched and the create parameter is set
to TRUE, a new resource is created with these specified resource IDs set on it. If the
system.displayname and/or system.hostname property is included as resource IDs, they will be used as
host name and display name respectively in the resulting resource.

<b>Name(String):</b> <br>Resource unique name. Only considered when creating a new resource.

<b>Properties(Dictonary<String, String>):</b> <br>New properties for resource. Updates to existing
resource properties are not considered. Depending on the property name, we will convert these
properties into system, auto, or custom properties.

<b>Description(String):</b>  <br>Resource description. Only considered when creating a new resource.

<b>Create(bool):</b> <br>Do you want to create the resource.

- ### DataSource

```java
DataSource dataSource = new DataSource(dataSourceName, dataSourceGroup, displayName, id);
```

<b>Name(String):</b>  <br>DataSource unique name. Used to match an existing DataSource. If no existing
DataSource matches the name provided here, a new DataSource is created with this name.

<b>DisplayName(String):</b> <br>DataSource display name. Only considered when creating a new DataSource.

<b>Group(string):</b> <br>DataSource group name. Only considered when DataSource does not already belong
to a group. Used to organize the DataSource within a DataSource group. If no existing DataSource
group matches, a new group is created with this name and the DataSource is organized under the new
group.

<b>Id(int):</b> <br>DataSource unique ID. Used only to match an existing DataSource. If no existing
DataSource matches the provided ID, an error results.

- ### DatasourceInstance

```java
DataSourceInstance dataSourceInstance = new DataSourceInstance(name, displayName, description, properties);
```

<b>Name(String):</b> <br>Instance name. If no existing instance matches, a new instance is created with
this name.

<b>DisplayName(String):</b> <br>Instance display name. Only considered when creating a new instance.

<b>Properties(Dictionary<String, String>):</b> <br>New properties for instance. Updates to existing
instance properties are not considered. Depending on the property name, we will convert these
properties into system, auto, or custom properties.

<b>Description(String):</b>  <br>Resource description. Only considered when creating a new resource.

- ### DataPoint

```java
DataPoint dataPoint = new DataPoint(name, description, aggregationType, description);
```

<b>Name(String):</b><br> Datapoint name. If no existing datapoint matches for specified DataSource, a
new datapoint is created with this name.

<b>AggregationType(String):</b><br>The aggregation method, if any, that should be used if data is pushed
in sub-minute intervals. Allowed options are “sum”, “average” , "percentile" and “none”(default) where “none” would
take last value for that minute. Only considered when creating a new datapoint. See the About the
Push Metrics REST API section of this guide for more information on datapoint value aggregation
intervals.

<b>Description(String):</b> <br>Datapoint description. Only considered when creating a new datapoint.

<b>Type(String):</b><br> Metric type as a number in String format. Allowed options are “guage” (default)
and “counter”. Only considered when creating a new datapoint.

- ### Value

```java
Map<String, String> value = new HashMap<>();
```

Value is a dictionary which stores the time of data emittion(in epoch) as Key of dictionary and
Metric Data as Value of dictionary.

<a name="documentation-for-api-endpoints"></a>

