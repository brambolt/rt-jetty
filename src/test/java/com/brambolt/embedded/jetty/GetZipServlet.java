package com.brambolt.embedded.jetty;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.brambolt.embedded.util.zip.ZipFiles.bytes;
import static java.nio.charset.StandardCharsets.UTF_8;

public class GetZipServlet extends HttpServlet {

    public static final String URI_PATH ="/hello/world/zip";

    public static final String ZIP_PATH = "content.txt";

    public static EmbeddedServer.Mapping<?> createMapping() {
        return new EmbeddedServer.Mapping<>(GetZipServlet.class, URI_PATH);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String content = "Hello world!";
        try {
            byte[] bytes = bytes(content, ZIP_PATH, UTF_8);
            Responses.zip(request, response, bytes, UTF_8);
        } catch (Throwable t) {
            Responses.internalError(request, response, t);
        }
    }
}
