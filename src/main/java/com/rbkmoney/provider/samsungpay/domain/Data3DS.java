package com.rbkmoney.provider.samsungpay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 05.07.18.
 */
public class Data3DS {
    @JsonProperty(value = "type", required = true)
    public String type;

    @JsonProperty(value = "version", required = true)
    public String version;

    @JsonProperty(value = "data", required = true)
    public String data;

    @JsonCreator
    public Data3DS(
            @JsonProperty(value = "type", required = true) String type,
            @JsonProperty(value = "version", required = true) String version,
            @JsonProperty(value = "data", required = true) String data
    ) {
        this.type = type;
        this.version = version;
        this.data = data;
    }
}
