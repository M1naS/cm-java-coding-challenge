package com.crewmeister.cmcodingchallenge.integration.bundesbank;

import com.crewmeister.cmcodingchallenge.integration.ExchangeRateDto;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankExchangeDto;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankExchangeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BundesbankExchangeRateServiceTest {

    @Mock
    BundesbankCacheStore bundesbankCacheStore;
    @Mock
    BundesbankExchangeRateProvider bundesbankExchangeRateProvider;

    @InjectMocks
    BundesbankExchangeRateService bundesbankExchangeRateService;

    @Test
    void getConvertedForeignExchangeAmount() {
        BundesbankExchangeDto mockData = new BundesbankExchangeDto();
        mockData.setDate(LocalDate.parse("2026-01-29"));
        List<ExchangeRateDto> mockExchangeRates = new ArrayList<>();
        mockExchangeRates.add(new ExchangeRateDto("USD", BigDecimal.valueOf(1.1968)));
        mockData.setRates(mockExchangeRates);


        when(bundesbankExchangeRateProvider.getExchangeRatesByDate(
                BundesbankExchangeRequest.builder().date(LocalDate.parse("2026-01-29")).build()
        )).thenReturn(mockData);

        BigDecimal result =
                bundesbankExchangeRateService.getConvertedForeignExchangeAmount(
                        LocalDate.parse("2026-01-29"),
                        "USD",
                        BigDecimal.valueOf(10)
                );

        assertEquals(BigDecimal.valueOf(8.36), result);
    }
}