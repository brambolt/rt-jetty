package com.brambolt.embedded.jetty;

import com.brambolt.embedded.json.Json;
import com.brambolt.util.Resources;
import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import static com.brambolt.embedded.jetty.ContentTypes.APPLICATION_JSON;
import static com.brambolt.embedded.jetty.ContentTypes.APPLICATION_ZIP;

public class Responses {

    public static void json(HttpServletRequest request, HttpServletResponse response, JsonNode node) throws IOException {
        json(request, response, Json.generate(node), StandardCharsets.UTF_8);
    }

    public static void json(HttpServletRequest request, HttpServletResponse response, String json, Charset charset) throws IOException {
        response.setStatus(SC_OK);
        response.setContentType(APPLICATION_JSON);
        response.setCharacterEncoding(charset.name());
        response.getWriter().println(json);
    }

    public static void zip(HttpServletRequest request, HttpServletResponse response, byte[] bytes, Charset charset) throws IOException {
        file(request, response, APPLICATION_ZIP, "content.zip", bytes, charset);
    }

    public static void file(HttpServletRequest request, HttpServletResponse response, String contentType, String basename, byte[] bytes, Charset charset) throws IOException {
        file(request, response, contentType, basename, bytes, bytes.length, charset);
    }

    public static void file(HttpServletRequest request, HttpServletResponse response, String contentType, String basename, byte[] bytes, int length, Charset charset) throws IOException {
        response.setStatus(SC_OK);
        response.setContentType(contentType);
        response.setContentLength(length);
        response.setCharacterEncoding(charset.name());
        String contentDisposition = String.format("attachment;filename=\"%s\"", basename);
        response.setHeader("Content-Disposition", contentDisposition);
        ServletOutputStream os = response.getOutputStream();
        os.write(bytes);
        os.flush();
    }

    public static void notFound(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(SC_NOT_FOUND);
    }

    public static void internalError(HttpServletRequest request, HttpServletResponse response) {
        internalError(request, response, null);
    }

    public static void internalError(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        response.setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    public static void staticResource(HttpServletRequest request, HttpServletResponse response, String contentType, String path, Charset charset) {
        String content = Resources.scan(path, charset);
        if (null != content)
            stringContent(request, response, contentType, content, charset);
         else staticResourceNotFound(request, response, path);
    }

    public static void staticResourceNotFound(HttpServletRequest request, HttpServletResponse response, String path) {
        notFound(request, response);
    }

    public static void stringContent(HttpServletRequest request, HttpServletResponse response, String contentType, String content, Charset charset) {
        response.setStatus(SC_OK);
        response.setContentType(contentType);
        response.setCharacterEncoding(charset.name());
        try {
            byte[] bytes = content.getBytes(charset.name());
            response.setContentLength(bytes.length);
            ServletOutputStream os = response.getOutputStream();
            os.write(bytes);
            os.flush();
        } catch (Throwable t) {
            internalError(request, response, t);
        }
    }
}

