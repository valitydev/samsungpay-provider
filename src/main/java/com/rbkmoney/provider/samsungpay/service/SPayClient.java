package com.rbkmoney.provider.samsungpay.service;

import com.rbkmoney.woody.api.flow.error.WErrorDefinition;
import com.rbkmoney.woody.api.flow.error.WRuntimeException;
import com.rbkmoney.woody.api.trace.context.TraceContext;
import com.rbkmoney.woody.thrift.impl.http.error.THTransportErrorMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by vpankrashkin on 26.06.18.
 */
public class SPayClient {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final int connTimeoutMs;
    private final int readTimeoutMs;
    private final int writeTimeoutMs;
    private final THTransportErrorMapper errorMapper;
    private final UriTemplate transactionTemplate;
    private final UriTemplate credentialsTemplate;

    public SPayClient(String transactionURLTemplate, String credentialsURLTemplate, int connTimeoutMs, int readTimeoutMs, int writeTimeoutMs) {
        this.connTimeoutMs = connTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
        this.writeTimeoutMs = writeTimeoutMs;
        this.transactionTemplate = new UriTemplate(transactionURLTemplate);
        this.credentialsTemplate = new UriTemplate(credentialsURLTemplate);
        this.errorMapper = new THTransportErrorMapper();
    }

    public String requestTransaction(String body) throws SPException {
        log.info("Create transaction with: {}", body);
        try {
            OkHttpClient client = prepareClient();
            log.debug("Http client prepared");
            Request request = preparePostRequest(transactionTemplate.expand(Collections.emptyMap()).toURL(), body);
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                log.warn("Unsuccessful call result");
            }
            String result = response.body().string();
            log.info("Create transaction result: {}", result);
            return result;
        } catch (IOException e) {
            WErrorDefinition errDef = errorMapper.mapToDef(e, TraceContext.getCurrentTraceData().getActiveSpan());
            if (errDef != null) {
                throw new WRuntimeException(e, errDef);
            } else {
                throw new WRuntimeException(e, new WErrorDefinition());
            }
        }
    }

    public String requestCredentials(String serviceId, String refId) throws Exception {
        log.info("Get credentials for srv:{}, ref:{}", serviceId, refId);
        try {
            OkHttpClient client = prepareClient();
            log.debug("Http client prepared");
            Request request = prepareGetRequest(credentialsTemplate.expand(new HashMap() {{
                put("id", refId);
                put("serviceId", serviceId);
            }}).toURL());
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new SPException("Unsuccessful call result", response.body().string());
            }
            String result = response.body().string();
            log.info("Credentials result: {}", result);
            return result;
        } catch (IOException e) {
            WErrorDefinition errDef = errorMapper.mapToDef(e, TraceContext.getCurrentTraceData().getActiveSpan());
            if (errDef != null) {
                throw new WRuntimeException(e, errDef);
            } else {
                throw new WRuntimeException(e, new WErrorDefinition());
            }
        }
    }

    private OkHttpClient prepareClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(connTimeoutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeoutMs, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeoutMs, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(c ->
                    c.proceed(
                            c.request().newBuilder()
                                    .addHeader("X-Request-Id", TraceContext.getCurrentTraceData().getActiveSpan().getSpan().getTraceId())
                                    .build()
                    )
                )
                .addInterceptor(new HttpLoggingInterceptor()).build();
    }

    private Request preparePostRequest(URL url, String body) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), body);
        return new Request.Builder().url(url).method("POST", requestBody).build();
    }

    private Request prepareGetRequest(URL url) {
        return new Request.Builder().url(url).method("GET", null).build();
    }

}
