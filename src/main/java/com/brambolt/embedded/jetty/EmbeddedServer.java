package com.brambolt.embedded.jetty;

import java.util.List;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.brambolt.util.Context;

import static com.brambolt.util.Require.notEmpty;
import static com.brambolt.util.Require.notNull;

public class EmbeddedServer {

    public static class Mapping<T extends Servlet> {

        public final Class<T> servletClass;

        public final String path;

        public Mapping(Class<T> servletClass, String path) {
            notNull(servletClass);
            notNull(path);
            this.servletClass = servletClass;
            this.path = path;
        }
    }

    private static EmbeddedServer instance = null;

    public static void create(int port, List<Mapping<? extends Servlet>> mappings, Context context) {
        if (null != instance)
            throw new IllegalStateException();
        instance = new EmbeddedServer(port, mappings, context);
    }

    public static void createAndStart(int port, List<Mapping<? extends Servlet>> mappings, Context context) throws Exception {
        create(port, mappings, context);
        instance.start(context);
    }

    public static EmbeddedServer getInstance() {
        notNull(instance);
        return instance;
    }

    private final Server wrapped;

    private final int port;

    private EmbeddedServer(int port, List<Mapping<? extends Servlet>> mappings, Context context) {
        System.out.println(String.format("Creating embedded Jetty server on port %d...", port));
        assert(0 < port);
        this.port = port;
        this.wrapped = createServer(port, mappings, context);
        notNull(this.wrapped);
        System.out.println(String.format("Created embedded Jetty server on port %d.", port));
    }

    public Server getWrapped() {
        return wrapped;
    }

    public int getPort() {
        return port;
    }

    protected Server createServer(int port, List<Mapping<? extends Servlet>> mappings, Context context) {
        Server server = new Server(port);
        server.setHandler(createHandler(mappings, context));
        return server;
    }

    protected ServletHandler createHandler(List<Mapping<? extends Servlet>> mappings, Context context) {
        notEmpty(mappings);
        ServletHandler handler = new ServletHandler();
        addServlets(handler, mappings, context);
        return handler;
    }

    protected void addServlets(ServletHandler handler, List<Mapping<? extends Servlet>> mappings, Context context) {
        for (Mapping mapping: mappings) {
            addServlet(handler, (Mapping<? extends Servlet>) mapping, context);
        }
    }

    protected void addServlet(ServletHandler handler, Mapping<? extends Servlet> mapping, Context context) {
        handler.addServletWithMapping(mapping.servletClass, mapping.path);
        ServletHolder[] holders = handler.getServlets();
        System.out.println(String.format("Added path mapping for %s [%s].", mapping.path, mapping.servletClass));
    }

    public void start(Context context) throws Exception {
        wrapped.start();
        System.out.println(String.format("Started embedded Jetty server on port %d.", port));
    }

    public void join(Context context) throws InterruptedException {
        wrapped.join();
    }

    public void startAndJoin(Context context) throws Exception {
        start(context);
        join(context);
    }

    public void stop(Context context) throws Exception {
        getWrapped().stop();
    }
}
