package com.brambolt.embedded.jetty;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.brambolt.embedded.jetty.ContentTypes.TEXT_HTML;
import static java.nio.charset.StandardCharsets.UTF_8;

public class GetHtmlServlet extends HttpServlet {

    public static final String PATH ="/hello/world";

    public static EmbeddedServer.Mapping<?> createMapping() {
        return new EmbeddedServer.Mapping<>(GetHtmlServlet.class, PATH);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            Responses.staticResource(
                request, response, TEXT_HTML,
                "com/brambolt/embedded/jetty/test/hello-world.html",
                UTF_8);
        } catch (Throwable t) {
            Responses.internalError(request, response, t);
        }
    }
}
