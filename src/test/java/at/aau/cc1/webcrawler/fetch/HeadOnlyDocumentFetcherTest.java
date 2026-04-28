package at.aau.cc1.webcrawler.fetch;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static at.aau.cc1.webcrawler.TestingAdapters.loadDocument;
import static org.junit.jupiter.api.Assertions.*;

public class HeadOnlyDocumentFetcherTest {
    private DocumentFetcher documentFetcher;

    @BeforeEach
    public void setupDocumentFetcher() throws Exception {
        DocumentAdapter document = loadDocument(this, "simpleDocument.html");
        documentFetcher = new HeadOnlyDocumentFetcher(new TestingDocumentFetcher("https://example.org/", document));
    }

    @Test
    public void testBodyTruncated() throws IOException {
        DocumentAdapter document = documentFetcher.fetchDocument("https://example.org/");
        assertNotNull(document);
        assertTrue(document.select("body").isEmpty());
        assertTrue(document.select("p").isEmpty());
        assertTrue(document.select("a").isEmpty());
        assertTrue(document.select("#testLink").isEmpty());
    }

    @Test
    public void testHeadIntact() throws IOException {
        DocumentAdapter document = documentFetcher.fetchDocument("https://example.org/");
        assertNotNull(document);
        assertEquals("Simple Document", document.title());
    }
}
