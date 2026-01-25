package com.crewmeister.cmcodingchallenge.integration.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BundesbankCurrencyRequest implements CurrencyRequest {
    private final String lang;
}
