package com.brambolt.embedded.jetty;

import com.brambolt.util.Context;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.brambolt.embedded.jetty.ContentTypes.APPLICATION_ZIP;
import static com.brambolt.embedded.jetty.ContentTypes.TEXT_HTML;
import static com.brambolt.embedded.jetty.GetZipMultipleEntriesServlet.PATHS;
import static com.brambolt.embedded.jetty.GetZipMultipleEntriesServlet.CONTENT;
import static com.brambolt.embedded.jetty.GetZipServlet.ZIP_PATH;
import static com.brambolt.util.Streams.scan;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

public class EmbeddedServerTest {

    private static EmbeddedServer server;

    private static String uriPrefix;

    private static HttpClient client;

    @BeforeAll
    public static void createAndStartServer() throws Exception {
        Context context = Context.create(EmbeddedServerTest.class);
        List<EmbeddedServer.Mapping<?>> mappings = new ArrayList<>();
        mappings.add(GetHtmlServlet.createMapping());
        mappings.add(GetZipServlet.createMapping());
        mappings.add(GetZipMultipleEntriesServlet.createMapping());
        mappings.add(PostZipServlet.createMapping());
        int port = 9006;
        EmbeddedServer.createAndStart(port, mappings, context);
        server = EmbeddedServer.getInstance();
        uriPrefix = String.format("http://localhost:%d", port);
    }

    @AfterAll
    public static void stopServer() throws Exception {
        Context context = Context.create(EmbeddedServerTest.class);
        server.stop(context);
        server = null;
    }

    /**
     * <pre>
     *   https://www.eclipse.org/jetty/documentation/9.4.x/http-client.html
     * </pre>
     * @throws Exception If unable to create the client
     */
    @BeforeAll
    public static void createAndStartClient() throws Exception {
        client = new HttpClient();
        client.start();
    }

    @AfterAll
    public static void stopClient() throws Exception {
        client.stop();
        client = null;
    }

    @Test
    public void test404() throws Exception {
        Context context = Context.create(EmbeddedServerTest.class);
        String uri = String.format("http://localhost:%d%s", server.getPort(), "/not/found");
        ContentResponse response = client.GET(uri);
        assert(SC_NOT_FOUND == response.getStatus());
    }

    @Test
    public void testGetHtml() throws Exception {
        Context context = Context.create(EmbeddedServerTest.class);
        String uri = uriPrefix + GetHtmlServlet.PATH;
        ContentResponse response = client.GET(uri);
        assert(SC_OK == response.getStatus());
        assert(TEXT_HTML.equals(response.getMediaType()));
        assert(UTF_8.name().equals(response.getEncoding()));
        assert(response.getContentAsString().contains("Hello world!"));
    }

    @Test
    public void testGetSingleFileZipAttachment() throws Exception {
        Context context = Context.create(EmbeddedServerTest.class);
        String uri = uriPrefix + GetZipServlet.URI_PATH;
        ContentResponse response = client.GET(uri);
        assert(SC_OK == response.getStatus());
        assert(APPLICATION_ZIP.equals(response.getMediaType()));
        assert(UTF_8.name().equalsIgnoreCase(response.getEncoding()));
        byte[] bytes = response.getContent();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        BufferedInputStream bis = new BufferedInputStream(bais);
        ZipInputStream zis = new ZipInputStream(bis);
        ZipEntry entry = zis.getNextEntry();
        assert(ZIP_PATH.equals(entry.getName()));
        long size = entry.getSize();
        // The scan closes the zip stream:
        String content = scan(zis, UTF_8);
        assert("Hello world!".equals(content));
    }

    @Test
    public void testGetMultiFileZipAttachment() throws Exception {
        Context context = Context.create(EmbeddedServerTest.class);
        String uri = uriPrefix + GetZipMultipleEntriesServlet.PATH;
        ContentResponse response = client.GET(uri);
        assert(SC_OK == response.getStatus());
        assert(APPLICATION_ZIP.equals(response.getMediaType()));
        assert(UTF_8.name().equalsIgnoreCase(response.getEncoding()));
        byte[] bytes = response.getContent();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        BufferedInputStream bis = new BufferedInputStream(bais);
        ZipInputStream zis = new ZipInputStream(bis);
        try (FilterInputStream fis = new FilterInputStream(zis) {
            @Override
            public void close() throws IOException {
                zis.closeEntry();
            }
        }) {
            for (int i = 0; i < PATHS.length; ++i) {
                ZipEntry entry = zis.getNextEntry();
                assert (PATHS[i].equals(entry.getName()));
                long size = entry.getSize();
                // The scan closes the zip entry:
                String content = scan(fis, UTF_8);
                assert (CONTENT[i].equals(content));
            }
            assert(null == zis.getNextEntry());
        }
    }

/*

    @Test
    public void testPostZipFile() throws Exception {
        Context context = Context.create(EmbeddedServerTest.class);
        String uri = uriPrefix + PostZipServlet.PATH;
        File zip = ZipFiles.file(CONTENT, GetZipMultipleEntriesServlet.PATHS, UTF_8);
        ContentResponse postResponse = client.POST(uri)
            .file(zip.toPath(), APPLICATION_ZIP)
            .send();

        assert(SC_OK == postResponse.getStatus());
        Thread.sleep(1000);
        ContentResponse getResponse = client.GET(uri);
        assert(SC_OK == getResponse.getStatus());
        byte[] bytes = getResponse.getContent();
        String[] unzipped = ZipFiles.scan(bytes, UTF_8);
        assert(CONTENT.length == unzipped.length);
        for (int i = 0; i < unzipped.length; ++i)
            assert(CONTENT[i].equals(unzipped[i]));
    }
*/

/*

  https://stackoverflow.com/questions/7255519/how-to-do-multipart-form-data-post-in-jetty-httpclient?rq=1
  https://ursaj.com/upload-files-in-java-with-servlet-api
  https://blog.fossasia.org/tag/jetty-file-upload/
  https://webtide.com/the-new-jetty-9-http-client/
  https://www.eclipse.org/jetty/documentation/9.4.x/http-client-api.html
  https://stackoverflow.com/questions/14525982/getting-request-payload-from-post-request-in-java-servlet
  http://www.eclipse.org/jetty/documentation/9.4.12.v20180830/
  http://www.eclipse.org/jetty/documentation/9.4.12.v20180830/embedded-examples.html
  https://www.eclipse.org/jetty/documentation/9.4.x/quickstart-config-what.html
  https://stackoverflow.com/questions/17652530/how-to-implement-fileupload-in-embedded-jetty
  https://stackoverflow.com/questions/30653012/multipart-form-data-no-injection-source-found-for-a-parameter-of-type-public-ja
  https://stackoverflow.com/questions/4996586/jetty-servlettester-to-post-a-file
  https://stackoverflow.com/questions/18020437/no-multipartconfig-for-servlet-error-from-jetty-using-scalatra
  https://bugs.eclipse.org/bugs/show_bug.cgi?id=395000
  https://github.com/dekellum/jetty/blob/master/jetty-servlets/src/test/java/org/eclipse/jetty/servlets/MultipartFilterTest.java


    @Test
    public void testPostMultiPart() throws Exception {
        Context context = Context.create(EmbeddedServerTest.class);
        String uri = uriPrefix + PostZipServlet.PATH;
        File zip = ZipFiles.file(CONTENT, GetZipMultipleEntriesServlet.PATHS, UTF_8);

        MultiPartContentProvider multiPart = new MultiPartContentProvider();
        multiPart.addFilePart(
            "zip",
            zip.getName(),
            new PathContentProvider(zip.toPath()),
            null);
        multiPart.close();
        ContentResponse postResponse = client.POST(uri)
            // .header("Content-Type", APPLICATION_ZIP)
            .content(multiPart)
            .send();

        assert(SC_OK == postResponse.getStatus());
        Thread.sleep(1000);
        ContentResponse getResponse = client.GET(uri);
        assert(SC_OK == getResponse.getStatus());
        byte[] bytes = getResponse.getContent();
        String[] unzipped = ZipFiles.scan(bytes, UTF_8);
        assert(CONTENT.length == unzipped.length);
        for (int i = 0; i < unzipped.length; ++i)
            assert(CONTENT[i].equals(unzipped[i]));
    }

        MultiPartContentProvider multiPart = new MultiPartContentProvider();
        multiPart.addFilePart("icon", "img.png", new PathContentProvider(Paths.get("/tmp/img.png")), null);
        multiPart.close();

 */

}
