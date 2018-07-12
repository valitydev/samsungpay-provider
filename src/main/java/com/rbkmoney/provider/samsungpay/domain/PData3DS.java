package com.rbkmoney.provider.samsungpay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.rbkmoney.provider.samsungpay.service.ExpDateDeserialiser;

import java.time.LocalDate;

/**
 * Created by vpankrashkin on 05.07.18.
 */
public class PData3DS {
    public long amount;
    public String currencyCode;
    public String created;
    public String eci;
    public String dpan;
    public LocalDate expirationDate;
    public String cryptogram;
    public String cardholder;

    @JsonCreator
    public PData3DS(
            @JsonProperty(value = "amount", required = true) long amount,
            @JsonProperty(value = "currency_code", required = true) String currencyCode,
            @JsonProperty(value = "utc", required = true) String created,
            @JsonProperty(value = "eci_indicator") String eci,
            @JsonProperty(value = "tokenPAN", required = true) String dpan,
            @JsonDeserialize(using = ExpDateDeserialiser.class)
            @JsonProperty(value = "tokenPanExpiration", required = true) LocalDate expirationDate,
            @JsonProperty(value = "cryptogram", required = true) String cryptogram,
            @JsonProperty(value = "cardholder_name") String cardholder) {
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.created = created;
        this.eci = eci;
        this.dpan = dpan;
        this.expirationDate = expirationDate;
        this.cryptogram = cryptogram;
        this.cardholder = cardholder;
    }

    @Override
    public String toString() {
        return "PData3DS{" +
                "amount=" + amount +
                ", currencyCode='" + currencyCode + '\'' +
                ", created='" + created + '\'' +
                ", eci='" + eci + '\'' +
                ", dpan='" + (dpan == null ? null : "***") + '\'' +
                ", expirationDate=" + (expirationDate == null ? null : "***") +
                ", cryptogram='" + (cryptogram == null ? null : "***") + '\'' +
                ", cardholder='" + (cardholder == null ? amount : "***") + '\'' +
                '}';
    }
}
