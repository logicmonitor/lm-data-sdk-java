package com.logicmonitor.sdk.data.example;

import com.logicmonitor.sdk.data.Configuration;
import com.logicmonitor.sdk.data.api.Metrics;
import com.logicmonitor.sdk.data.model.DataPoint;
import com.logicmonitor.sdk.data.model.DataSource;
import com.logicmonitor.sdk.data.model.DataSourceInstance;
import com.logicmonitor.sdk.data.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.client.ApiCallback;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;

import java.time.Instant;
import java.util.*;

@Slf4j
public class MetricsIngestion {


    public static void main(final String[] args) throws InterruptedException {

        final String resourceName = "Java_Data_SDK_Test";
        final HashMap<String, String> resourceIds = new HashMap<>();
        resourceIds.put("system.displayname", resourceName);

        final HashMap<String, String> resourceProperties = new HashMap<>();
        resourceProperties.put("resourceProperties", "Java_Data_SDK_Resource_Property");

        final String instanceName = "Java_Data_SDK_Instance";

        final HashMap<String, String> instanceProperties = new HashMap<>();
        instanceProperties.put("instanceProperties", "Java_Data_SDK_Instance_Property");

        final String dataSourceGroup = "Java Data SDK Group";
        final String dataSourceName = "Java Data SDK";
        final String cpuUsage = "cpuUsage";
        final boolean batch = false;

        final Resource resource = Resource.builder().ids(resourceIds).name(resourceName).properties(resourceProperties).build();
        final DataSource dataSource = DataSource.builder().name(dataSourceName).group(dataSourceGroup).singleInstanceDS(false).build();
        final DataSourceInstance dataSourceInstance = DataSourceInstance.builder().name(instanceName).properties(instanceProperties).build();
        final DataPoint dataPoint = DataPoint.builder().name(cpuUsage).build();
        final Map<String, String> cpuUsageValue = new HashMap<>();
        syncExample(resource, dataSource, dataSourceInstance, dataPoint, cpuUsageValue, batch);
    }

    private static void syncExample(final Resource resource, final DataSource dataSource,
                                    final DataSourceInstance dataSourceInstance, final DataPoint dataPoint,
                                    final Map<String, String> cpuUsageValue, boolean batch) throws InterruptedException {
        final Configuration conf = new Configuration();
        MyResponse responseInterface = new MyResponse();
        final Metrics metrics = new Metrics(conf, 10, batch, responseInterface);
        while (true) {
            final String cpuUsageMetric = String.valueOf(new Random().nextInt(100 - 10) + 10);
            cpuUsageValue.put(String.valueOf(Instant.now().getEpochSecond()), cpuUsageMetric);
            final DataPoint dataPoint1 = DataPoint.builder().name("cpuUsageValue_open").build();
            final DataPoint dataPoint2 = DataPoint.builder().name("cpuUsageValue_AAPL").build();
            try {

                Optional<ApiResponse<String>> response = metrics.sendMetrics(resource, dataSource, dataSourceInstance, dataPoint, cpuUsageValue);
                if (response != null && response.isPresent()) {
                    log.debug(
                            "Response: Status: " + response.get().getStatusCode() + " Headers: "
                                    + response.get().getHeaders() + " Data: " + response.get().getData());
                }
                metrics.updateResourceProperties(resource.getIds(), resource.getProperties(), true);
               metrics.updateInstanceProperties(resource.getIds(), dataSource.getName(),dataSource.getDisplayName(), dataSourceInstance.getName(),dataSourceInstance.getProperties(),true);

                response = metrics.sendMetrics(resource, dataSource, dataSourceInstance, dataPoint1, cpuUsageValue);
                if (response != null && response.isPresent()) {
                    log.debug(
                            "Response: Status: " + response.get().getStatusCode() + " Headers: "
                                    + response.get().getHeaders() + " Data: " + response.get().getData());
                }
                response = metrics.sendMetrics(resource, dataSource, dataSourceInstance, dataPoint2, cpuUsageValue);
                if (response != null && response.isPresent()) {
                    log.debug(
                             "Response: Status: " + response.get().getStatusCode() + " Headers: "
                                    + response.get().getHeaders() + " Data: " + response.get().getData());
                }
            } catch (final Exception e) {
                log.error("Exception while sending metrics.", e);
            }
            cpuUsageValue.clear();
            Thread.sleep(1 * 1000);
        }
    }

    static class MyResponse implements ApiCallback<ApiResponse> {
        @Override
        public void onFailure(ApiException e, int statusCode, Map responseHeaders) {
            log.error("Failed to process: statusCode =" + statusCode + "\nmessage = " +e.getMessage()+ "\nresponseHeaders = "+ e.getResponseHeaders()+ "\nresponseBody = ", e.getResponseBody());
        }

        @Override
        public void onSuccess(ApiResponse result, int statusCode, Map responseHeaders) {
            log.debug("Successfully processed : statusCode = " + statusCode + " \nresponseBody = " + result.getData() + " \nresponseHeaders = "+responseHeaders);
        }

        @Override
        public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
        }

        @Override
        public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
        }
    }
}
