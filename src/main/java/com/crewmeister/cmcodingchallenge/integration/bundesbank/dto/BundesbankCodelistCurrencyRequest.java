package com.crewmeister.cmcodingchallenge.integration.bundesbank.dto;

import com.crewmeister.cmcodingchallenge.integration.CurrencyRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BundesbankCodelistCurrencyRequest implements CurrencyRequest {
    private final String lang;
}
