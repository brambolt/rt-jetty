package com.brambolt.embedded.jetty;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brambolt.util.Context;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class JsonServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Context context = Context.create(getClass(), null);
        try {
            Responses.json(request, response, process(Requests.json(request), context));
        } catch (Throwable t) {
            Responses.internalError(request, response, t);
        }
    }

    abstract protected JsonNode process(JsonNode input, Context context) throws IOException, ServletException;
}
