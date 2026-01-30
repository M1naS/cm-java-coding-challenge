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
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class BundesbankMapper implements IntegrationMapper {
    private final ObjectMapper jsonMapper;
    private final CsvMapper csvMapper;

    @Override
    public List<String> parseToAvailableCurrencies(InputStream csvInputStream) {
        List<String> currencyList = new ArrayList<>();

        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        try(MappingIterator<JsonNode> nodeIterator = csvMapper.readerFor(JsonNode.class)
                .with(schema)
                .readValues(csvInputStream)) {

            while (nodeIterator.hasNext()) {
                currencyList.add(nodeIterator.next().get("BBK_STD_CURRENCY").asText());
            }

            return currencyList;
        } catch (IOException ioException) {
            throw new SerializationException("Could not deserialize available currency list", ioException);
        }
    }

    @Override
    public BundesbankExchangeDto parseToExchangeRate(InputStream csvInputStream) {
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        try(MappingIterator<JsonNode> nodeIterator = csvMapper.readerFor(JsonNode.class)
                .with(schema)
                .readValues(csvInputStream)) {
            BundesbankExchangeDto bundesbankExchange = new BundesbankExchangeDto();
            while (nodeIterator.hasNext()) {
                List<ExchangeRateDto> exchangeRates = new ArrayList<>();

                JsonNode node = nodeIterator.next();

                String date = node.get("TIME_PERIOD").asText();

                if (bundesbankExchange.getRates() == null || bundesbankExchange.getRates().isEmpty()) {
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
                } else {
                    if (!node.get("OBS_STATUS").asText().equals("K")) {
                        bundesbankExchange.getRates().add(
                                new ExchangeRateDto(
                                        node.get("BBK_STD_CURRENCY").asText(),
                                        new BigDecimal(node.get("OBS_VALUE").asText())
                                )
                        );
                    }
                }
            }
            return bundesbankExchange;
        } catch (IOException ioException) {
            throw new SerializationException("Could not deserialize exchange rate list", ioException);
        }
    }

    @Override
    public List<BundesbankExchangeDto> parseToExchangeRateList(InputStream csvInputStream) {
        Map<LocalDate, List<ExchangeRateDto>> exchangeRatesMap = new TreeMap<>();

        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        try (MappingIterator<JsonNode> nodeIterator = csvMapper.readerFor(JsonNode.class)
                .with(schema)
                .readValues(csvInputStream)) {

            while (nodeIterator.hasNext()) {
                JsonNode node = nodeIterator.next();

                String date = node.get("TIME_PERIOD").asText();

                LocalDate parsedDate = LocalDate.parse(date);
                List<ExchangeRateDto> datesRates = exchangeRatesMap.getOrDefault(parsedDate, new ArrayList<>());

                if (datesRates.isEmpty()) {
                    if (!node.get("OBS_STATUS").asText().equals("K")) {
                        datesRates.add(
                                new ExchangeRateDto(
                                        node.get("BBK_STD_CURRENCY").asText(),
                                        new BigDecimal(node.get("OBS_VALUE").asText())
                                )
                        );
                    }

                    exchangeRatesMap.putIfAbsent(parsedDate, datesRates);
                } else {
                    if (!node.get("OBS_STATUS").asText().equals("K")) {
                        exchangeRatesMap.get(parsedDate).add(
                                new ExchangeRateDto(
                                        node.get("BBK_STD_CURRENCY").asText(),
                                        new BigDecimal(node.get("OBS_VALUE").asText())
                                )
                        );
                    }
                }
            }
            return exchangeRatesMap.entrySet().stream()
                    .filter(entry -> !entry.getValue().isEmpty())
                    .map(entry -> new BundesbankExchangeDto(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        } catch (IOException ioException) {
                throw new SerializationException("Could not deserialize exchange rate list", ioException);
        }
    }
}
