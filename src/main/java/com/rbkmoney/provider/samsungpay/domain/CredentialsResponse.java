package com.rbkmoney.provider.samsungpay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 05.07.18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CredentialsResponse {
    public String deviceId;
    public AuthMethod authMethod;
    public String last4digits;
    public String cardBrand;
    public Data3DS data3DS;
    public Certificate[] certificates;

    @JsonCreator
    public CredentialsResponse(
            @JsonProperty(value = "wallet_dm_id") String deviceId,
            @JsonProperty(value = "method", required = true) AuthMethod authMethod,
            @JsonProperty(value = "card_last4digits") String last4digits,
            @JsonProperty(value = "card_brand") String cardBrand,
            @JsonProperty(value = "3DS") Data3DS data3DS,
            @JsonProperty(value = "certificates") Certificate[] certificates) {
        this.deviceId = deviceId;
        this.authMethod = authMethod;
        this.last4digits = last4digits;
        this.cardBrand = cardBrand;
        this.data3DS = data3DS;
        this.certificates = certificates;
    }
}
