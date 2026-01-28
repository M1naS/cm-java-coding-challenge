package com.crewmeister.cmcodingchallenge.integration;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ExchangeRequest {
    Integer getLastNObservations();
    LocalDate getDate();
    String getCurrencyCode();
    BigDecimal getAmount();
}
