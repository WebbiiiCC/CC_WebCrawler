package at.aau.cc1.webcrawler.fetch;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;
import at.aau.cc1.webcrawler.adapter.ElementAdapter;
import at.aau.cc1.webcrawler.adapter.HttpStatusExceptionAdapter;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

public class DirectDocumentFetcherTest {
    private static final int PORT = 62626;
    private final DocumentFetcher documentFetcher = new DirectDocumentFetcher();

    private static HttpServer httpServer;

    @BeforeAll
    public static void setupHttpServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 5);
        httpServer.createContext("/index.html", ex -> {
            try (InputStream in = DirectDocumentFetcherTest.class.getResourceAsStream("simpleDocument.html");
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[256];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }

                byte[] bytes = out.toByteArray();
                ex.sendResponseHeaders(200, bytes.length);
                ex.getResponseBody().write(bytes);
                ex.getResponseBody().close();
            }
        });
        httpServer.createContext("/nonExistent.html", ex -> {
            ex.sendResponseHeaders(404, 0);
            ex.getResponseBody().close();
        });
        httpServer.start();
    }

    @AfterAll
    public static void stopHttpServer() {
        httpServer.stop(0);
        httpServer = null;
    }

    @Test
    public void testFetchSimpleDocument() throws IOException {
        DocumentAdapter document = documentFetcher.fetchDocument("http://localhost:" + PORT + "/index.html");
        assertNotNull(document);
        assertEquals("Simple Document", document.title());

        ElementAdapter testLink = document.select("#testLink").first();
        assertNotNull(testLink);
        assertEquals("https://example.org/", testLink.attr("href"));
        assertEquals("_blank", testLink.attr("target"));
        assertEquals("Example", testLink.innerText());
    }

    @Test
    public void testNonExistentDocument() {
        assertThrows(HttpStatusExceptionAdapter.class, () -> documentFetcher.fetchDocument("http://localhost:" + PORT + "/nonExistent.html"));
    }
}
