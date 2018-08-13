package com.rbkmoney.provider.samsungpay.service;

/**
 * Created by vpankrashkin on 09.07.18.
 */

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class ExpDateDeserialiser extends JsonDeserializer<YearMonth> {
    private final DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("yyMMdd");
    private final DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("MMyy");

    @Override
    public YearMonth deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        String value = node.asText();
        if (value.length() > 4) {
            return YearMonth.from(fullDateFormatter.parse(value));
        } else {
            return YearMonth.from(shortDateFormatter.parse(value));
        }

    }
}
