package com.crewmeister.cmcodingchallenge.integration;

import com.crewmeister.cmcodingchallenge.exception.SerializationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Getter
public class BundesbankMapper {
    private final CsvMapper csvMapper;

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
}
