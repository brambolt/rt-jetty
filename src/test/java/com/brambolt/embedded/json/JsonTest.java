package com.brambolt.embedded.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import org.junit.jupiter.api.Test;

import static com.brambolt.embedded.json.Json.generate;
import static com.brambolt.embedded.json.Json.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonTest {

    public static final String JSON_1 = "{\"a\":1}";
    @Test
    public void testCreate() {
        assertNotNull(Json.create());
    }

    @Test
    public void testParse1() throws IOException {
        assertNotNull(parse(JSON_1));
    }

    @Test
    public void testParse2() {
        // The key is missing the double parens:
        assertThrows(JsonParseException.class, () -> parse("{a:1}"));
    }

    @Test
    public void testGenerate1() throws IOException {
        assertEquals(JSON_1, generate(parse(JSON_1)));
    }
}
