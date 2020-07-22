package com.brambolt.embedded.jetty;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.util.Collection;

import static com.brambolt.embedded.jetty.Responses.internalError;
import static com.brambolt.embedded.util.zip.ZipFiles.scan;
import static java.nio.charset.StandardCharsets.UTF_8;

public class PostZipServlet extends HttpServlet {

    public static final String PATH ="/post/zip";

    public static EmbeddedServer.Mapping<?> createMapping() {
        return new EmbeddedServer.Mapping<>(PostZipServlet.class, PATH);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
/*        if (!APPLICATION_ZIP.equals(request.getContentType()))
            internalError(request, response);
        else {
 */           try {
                Collection<Part> parts = request.getParts();
                for (Part part: parts) {
                    String[] content = scan(part.getInputStream(), UTF_8);
                    System.out.println("");
                }
            } catch (Exception x) {
                internalError(request, response, x);
            }
        }
//    }
}

/*

                String[] content = scan(request.getInputStream(), UTF_8);
                System.out.println(content[0]);


 */
