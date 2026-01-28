package com.crewmeister.cmcodingchallenge.integration.bundesbank;

import com.crewmeister.cmcodingchallenge.exception.SerializationException;
import com.crewmeister.cmcodingchallenge.integration.ExchangeRateDto;
import com.crewmeister.cmcodingchallenge.integration.IntegrationMapper;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankExchangeDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class BundesbankMapper implements IntegrationMapper {
    private final ObjectMapper jsonMapper;
    private final CsvMapper csvMapper;

    @Override
    public List<String> parseToAvailableCurrencies(InputStream csvInputStream) {
        List<String> currencyList = new ArrayList<>();

        try {
            CsvSchema schema = CsvSchema.emptySchema().withHeader();

            MappingIterator<JsonNode> nodeIterator = csvMapper.readerFor(JsonNode.class)
                    .with(schema)
                    .readValues(csvInputStream);

            while (nodeIterator.hasNext()) {
                currencyList.add(nodeIterator.next().get("BBK_STD_CURRENCY").asText());
            }

            return currencyList;
        } catch (IOException ioException) {
            throw new SerializationException("Could not deserialize available currency list", ioException);
        }
    }

    @Override
    public List<BundesbankExchangeDto> parseToExchangeRate(InputStream csvInputStream) {
        List<BundesbankExchangeDto> bundesbankExchangeList = new ArrayList<>();

        try {
            CsvSchema schema = CsvSchema.emptySchema().withHeader();

            MappingIterator<JsonNode> nodeIterator = csvMapper.readerFor(JsonNode.class)
                    .with(schema)
                    .readValues(csvInputStream);

            while (nodeIterator.hasNext()) {
                BundesbankExchangeDto bundesbankExchange = new BundesbankExchangeDto();
                List<ExchangeRateDto> exchangeRates = new ArrayList<>();

                JsonNode node = nodeIterator.next();

                String date = node.get("TIME_PERIOD").asText();

                List<ExchangeRateDto> dateRates = bundesbankExchangeList.stream()
                        .filter(exchange -> exchange
                                .getDate()
                                .equals(LocalDate.parse(date))
                        )
                        .findFirst().map(BundesbankExchangeDto::getRates)
                        .orElse(Collections.emptyList());

                if (dateRates.isEmpty()) {
                    if (!node.get("OBS_STATUS").asText().equals("K")) {
                        exchangeRates.add(
                                new ExchangeRateDto(
                                        node.get("BBK_STD_CURRENCY").asText(),
                                        new BigDecimal(node.get("OBS_VALUE").asText())
                                )
                        );
                    }

                    bundesbankExchange.setDate(LocalDate.parse(date));
                    bundesbankExchange.setRates(exchangeRates);
                    bundesbankExchangeList.add(bundesbankExchange);
                } else {
                    if (!node.get("OBS_STATUS").asText().equals("K")) {
                        dateRates.add(
                                new ExchangeRateDto(
                                        node.get("BBK_STD_CURRENCY").asText(),
                                        new BigDecimal(node.get("OBS_VALUE").asText())
                                )
                        );
                    }
                }
                if (exchangeRates.isEmpty()) {
                    bundesbankExchangeList.remove(new BundesbankExchangeDto(LocalDate.parse(date), exchangeRates));
                }
            }
            return bundesbankExchangeList;
        } catch (IOException ioException) {
            throw new SerializationException("Could not deserialize exchange rate list", ioException);
        }
    }
}
