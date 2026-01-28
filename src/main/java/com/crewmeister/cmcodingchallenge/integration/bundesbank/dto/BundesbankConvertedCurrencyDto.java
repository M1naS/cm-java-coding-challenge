package com.crewmeister.cmcodingchallenge.integration.bundesbank.dto;

import com.crewmeister.cmcodingchallenge.integration.ConvertedCurrencyDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class BundesbankConvertedCurrencyDto implements ConvertedCurrencyDto {
    public LocalDate date;
    public BigDecimal rate;
    public String currencyCode;
    public BigDecimal amount;
    public BigDecimal converted;
}
