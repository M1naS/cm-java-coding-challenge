package com.crewmeister.cmcodingchallenge.integration.bundesbank.dto;

import com.crewmeister.cmcodingchallenge.integration.ConvertedCurrencyDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class BundesbankConvertedCurrencyDto implements ConvertedCurrencyDto {
    public final LocalDate date;
//    public final BigDecimal rate;
    public final String currencyCode;
    public final BigDecimal amount;
    public final BigDecimal converted;
}
