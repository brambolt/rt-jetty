package com.brambolt.embedded.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class Json {

    public static ObjectNode create() {
        return JsonNodeFactory.instance.objectNode();
    }

    public static JsonNode parse(String json) throws IOException {
        return new ObjectMapper().readTree(json);
    }

    public static String generate(JsonNode node) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(node);
    }
}
