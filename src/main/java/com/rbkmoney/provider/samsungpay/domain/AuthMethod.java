package com.rbkmoney.provider.samsungpay.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 05.07.18.
 */
public enum AuthMethod {
    @JsonProperty("3DS")
    Auth3DS
}
