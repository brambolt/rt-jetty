package com.brambolt.embedded.jetty;

import com.brambolt.embedded.json.Json;
import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class Requests {

    public static JsonNode json(HttpServletRequest request) throws IOException {
        return Json.parse(scan(request));
    }

    public static String scan(HttpServletRequest request) throws IOException {
        StringBuilder b = new StringBuilder();
        try (BufferedReader r = request.getReader()) {
            for (String l = r.readLine(); null != l; l = r.readLine()) {
                b.append(l).append("\n");
            }
            return b.toString();
        }
    }

    public static byte[] zip(HttpServletRequest request) throws IOException {
        throw new UnsupportedOperationException();
    }
}
