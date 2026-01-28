package com.crewmeister.cmcodingchallenge.integration.bundesbank.dto;

import com.crewmeister.cmcodingchallenge.integration.CurrencyDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BundesbankCodelistCurrencyDto implements CurrencyDto {
    public String name;

    @JsonProperty("id")
    public String code;
}
