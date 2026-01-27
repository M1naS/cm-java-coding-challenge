package com.crewmeister.cmcodingchallenge.integration.impl;

import com.crewmeister.cmcodingchallenge.exception.SerializationException;
import com.crewmeister.cmcodingchallenge.integration.IntegrationMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
public class BundesbankMapper implements IntegrationMapper {
    private final CsvMapper csvMapper;

    @Override
    public JsonNode parseToAvailableCurrencies(InputStream csvInputStream) {
        ArrayNode root = csvMapper.createArrayNode();

        try {
            CsvSchema schema = CsvSchema.emptySchema().withHeader();

            MappingIterator<JsonNode> nodeIterator = csvMapper.readerFor(JsonNode.class)
                    .with(schema)
                    .readValues(csvInputStream);

            while (nodeIterator.hasNext()) {
                root.add(nodeIterator.next().get("BBK_STD_CURRENCY").asText());
            }

            return root;
        } catch (IOException ioException) {
            throw new SerializationException("Could not deserialize available currency list", ioException);
        }
    }

    @Override
    public JsonNode parseToExchangeRate(InputStream csvInputStream) {
        ObjectNode root = csvMapper.createObjectNode();

        try {
            CsvSchema schema = CsvSchema.emptySchema().withHeader();

            MappingIterator<JsonNode> nodeIterator = csvMapper.readerFor(JsonNode.class)
                    .with(schema)
                    .readValues(csvInputStream);

            while (nodeIterator.hasNext()) {
                JsonNode node = nodeIterator.next();

                String date = node.get("TIME_PERIOD").asText();

                if (!root.has(date)) {
                    ObjectNode exchangeRateObject = csvMapper.createObjectNode();

                    if (!node.get("OBS_STATUS").asText().equals("K")) {

                        exchangeRateObject.put(
                                node.get("BBK_STD_CURRENCY").asText(),
                                new BigDecimal(node.get("OBS_VALUE").asText())
                        );
                    }

                    root.set(date, exchangeRateObject);
                } else {
                    ObjectNode dateObject = (ObjectNode) root.get(date);

                    if (!node.get("OBS_STATUS").asText().equals("K")) {
                        dateObject.put(
                                node.get("BBK_STD_CURRENCY").asText(),
                                new BigDecimal(node.get("OBS_VALUE").asText())
                        );
                    }
                }

                if (root.get(date).isEmpty()) root.remove(date);
            }

            return root;
        } catch (IOException ioException) {
            throw new SerializationException("Could not deserialize exchange rate list", ioException);
        }
    }
}
