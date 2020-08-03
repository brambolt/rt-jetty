package com.brambolt.embedded.jetty;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.brambolt.util.zip.ZipFiles.bytes;
import static java.nio.charset.StandardCharsets.UTF_8;

public class GetZipMultipleEntriesServlet extends HttpServlet {

    public static final String PATH ="/hello/world/zip/multiple";

    public static final String[] CONTENT = new String[] { "Hello Speedy!", "Arriba, arriba!" };

    public static final String[] PATHS = new String[] { "1/hello.txt", "2/arriba.txt" };

    public static EmbeddedServer.Mapping<?> createMapping() {
        return new EmbeddedServer.Mapping<>(GetZipMultipleEntriesServlet.class, PATH);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            byte[] bytes = bytes(CONTENT, PATHS, UTF_8);
            Responses.zip(request, response, bytes, UTF_8);
        } catch (Throwable t) {
            Responses.internalError(request, response, t);
        }
    }
}
