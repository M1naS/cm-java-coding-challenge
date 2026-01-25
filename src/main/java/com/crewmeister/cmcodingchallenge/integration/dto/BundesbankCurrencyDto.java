package com.crewmeister.cmcodingchallenge.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BundesbankCurrencyDto extends CurrencyDto {
    @JsonProperty("id")
    @Override
    public String getCode() {
        return super.getCode();
    }
}
