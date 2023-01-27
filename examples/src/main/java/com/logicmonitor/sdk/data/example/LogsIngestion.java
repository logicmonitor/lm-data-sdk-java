package com.logicmonitor.sdk.data.example;

import com.logicmonitor.sdk.data.Configuration;
import com.logicmonitor.sdk.data.api.Logs;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.client.ApiCallback;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class LogsIngestion {

    public static void main(final String[] args) throws InterruptedException {

        final String resourceName = "Java_Data_SDK_Test";
        final HashMap<String, String> resourceIds = new HashMap<>();
        resourceIds.put("system.displayname", resourceName);

        final Configuration conf = new Configuration();
        MyResponse responseInterface = new MyResponse();
        final Logs logs = new Logs(conf, 10, false, responseInterface);
        while (true) {
            try {
                Optional<ApiResponse> response = logs.sendLogs("Testing log Api", resourceIds, null,0L);
                if (response != null && response.isPresent()) {
                    log.debug(
                          "Response: Status: " + response.get().getStatusCode() + " Headers: "
                                    + response.get().getHeaders() + " Data: " + response.get().getData());
                }
                response = logs.sendLogs("Testing log Api second call", resourceIds,
                        null,1674036943L);
                if (response != null && response.isPresent()) {
                    log.debug("Response: Status: " + response.get().getStatusCode() + " Headers: "
                                    + response.get().getHeaders() + " Data: " + response.get().getData());
                }
            } catch (final ApiException | IOException e) {
                log.error("Exception while sending logs.", e);
            }
            Thread.sleep(5 * 1000);
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
