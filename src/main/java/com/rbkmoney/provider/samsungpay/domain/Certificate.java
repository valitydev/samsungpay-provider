package com.rbkmoney.provider.samsungpay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 05.07.18.
 */
public class Certificate {
    public String usage;
    public String alias;
    public String content;

    @JsonCreator
    public Certificate(
            @JsonProperty(value = "usage", required = true) String usage,
            @JsonProperty(value = "alias", required = true) String alias,
            @JsonProperty(value = "content", required = true) String content) {
        this.usage = usage;
        this.alias = alias;
        this.content = content;
    }
}
