package com.brambolt.embedded.jetty;

import java.util.ArrayList;
import java.util.List;

import com.brambolt.embedded.jetty.EmbeddedServer.Mapping;
import com.brambolt.embedded.util.Context;

/**
 * Starts a Jetty server.
 */
public class StartServer {

	/**
	 * Main entry point for the Jetty server.
	 *
	 * @param args Command-line arguments
	 */
	public static void main(String[] args) throws Exception {
		Context context = Context.create(StartServer.class);
        EmbeddedServer.createAndStart(9006, createMappings(), context);
        EmbeddedServer.getInstance().join(context);
	}

	public static List<Mapping<?>> createMappings() {
        List<Mapping<?>> mappings = new ArrayList<>();
        mappings.add(GetHtmlServlet.createMapping());
        mappings.add(GetZipServlet.createMapping());
	    return mappings;
    }
}

