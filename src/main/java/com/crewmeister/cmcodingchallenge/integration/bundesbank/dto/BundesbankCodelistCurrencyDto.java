package com.crewmeister.cmcodingchallenge.integration.bundesbank.dto;

import com.crewmeister.cmcodingchallenge.integration.CurrencyDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BundesbankCodelistCurrencyDto extends CurrencyDto {
    @JsonProperty("id")
    @Override
    public String getCode() {
        return super.getCode();
    }
}
