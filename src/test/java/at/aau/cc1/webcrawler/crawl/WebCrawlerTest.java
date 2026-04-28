package at.aau.cc1.webcrawler.crawl;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;
import at.aau.cc1.webcrawler.adapter.ElementAdapter;
import at.aau.cc1.webcrawler.fetch.TestingDocumentFetcher;
import at.aau.cc1.webcrawler.mapping.LinkMapper;
import at.aau.cc1.webcrawler.mapping.LocalLinkMapper;
import at.aau.cc1.webcrawler.mapping.TestingLinkTranslator;
import at.aau.cc1.webcrawler.mapping.translate.LinkTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static at.aau.cc1.webcrawler.TestingAdapters.loadDocument;
import static org.junit.jupiter.api.Assertions.*;

public class WebCrawlerTest {
    private TestingDocumentFetcher documentFetcher;
    private TestingReportLogger reportLogger;
    private TestingStorageTarget storageTarget;
    private WebCrawler webCrawler;

    @BeforeEach
    public void setupWebCrawler() throws Exception {
        DocumentAdapter document = loadDocument(this, "crawledDocument.html");
        LinkTranslator linkTranslator = new TestingLinkTranslator()
                .map("/assets/style.css", "assets/style.css");
        LinkMapper linkMapper = new LocalLinkMapper(linkTranslator);
        documentFetcher = new TestingDocumentFetcher("https://example.org/", document);
        reportLogger = new TestingReportLogger();
        storageTarget = new TestingStorageTarget();
        webCrawler = new WebCrawler(documentFetcher, reportLogger, linkMapper, storageTarget, "https://example.org");
    }

    @Test
    public void testDocumentsFetched() throws IOException {
        webCrawler.downloadPage("/", new File("/"), 5);

        assertEquals(1, documentFetcher.getFetches());
        assertEquals(1, documentFetcher.getErrors());
    }

    @Test
    public void testReportFinished() throws IOException {
        webCrawler.downloadPage("/", new File("/"), 5);

        assertTrue(reportLogger.isFinished());
        assertFalse(reportLogger.isInvocationsPastFinish());
    }

    @Test
    public void testReportCorrect() throws IOException {
        webCrawler.downloadPage("/", new File("/"), 5);

        String output = reportLogger.getOutput().toString();
        assertEquals("""
                # Crawl: https://example.org/
                ## /
                Depth: 0 \s
                ### This is a heading
                #### This is a subheading
                ### This is another big heading
                Link to external page: **https://example.com/** \s
                Link to path: **/assets/style.css** (rewritten: **assets/style.css**) \s
                ## /assets/style.css
                Depth: 1 \s
                Error fetching https://example.org/assets/style.css: HTTP Status Code 404 \s
                This link will stay broken in the local page! \s
                """, output);
    }

    @Test
    public void testCorrectDocumentStored() throws IOException {
        webCrawler.downloadPage("/", new File("/"), 5);

        assertEquals("/index.html", storageTarget.getStoredPath());

        DocumentAdapter document = storageTarget.getStoredDocument();
        assertNotNull(document);
        assertEquals("Simple Document", document.title());

        ElementAdapter headLink = document.select("html > head > link").first();
        assertNotNull(headLink);
        assertEquals("assets/style.css", headLink.attr("href"));
        assertEquals("stylesheet", headLink.attr("rel"));

        ElementAdapter testLink = document.select("#testLink").first();
        assertNotNull(testLink);
        assertEquals("https://example.com/", testLink.attr("href"));
        assertEquals("_blank", testLink.attr("target"));
    }
}
