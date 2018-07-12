package com.rbkmoney.provider.samsungpay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 05.07.18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultStatus {
    public String code;
    public String description;

    @JsonCreator
    public ResultStatus(
            @JsonProperty(value = "resultCode", required = true) String code,
            @JsonProperty(value = "resultMessage", required = true) String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return "ResultStatus{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
