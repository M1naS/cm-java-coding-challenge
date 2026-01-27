package com.crewmeister.cmcodingchallenge.integration.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BundesbankExchangeRequest implements ExchangeRequest {
    private final int lastNObservations;
}
