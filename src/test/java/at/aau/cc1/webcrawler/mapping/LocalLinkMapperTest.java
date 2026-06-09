package at.aau.cc1.webcrawler.mapping;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;
import at.aau.cc1.webcrawler.adapter.ElementAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static at.aau.cc1.webcrawler.TestingAdapters.loadDocument;
import static org.junit.jupiter.api.Assertions.*;

public class LocalLinkMapperTest {
    private final TestingLinkTranslator linkTranslator = new TestingLinkTranslator();
    private final LinkMapper linkMapper = new LocalLinkMapper(linkTranslator);

    @AfterEach
    public void resetLinkTranslator() {
        linkTranslator.reset();
    }

    @Test
    public void testHeadLinkReplacement() throws Exception {
        linkTranslator.map("/assets/style.css", "../assets/style.css")
                .map("/assets/script.js", "../assets/script.js")
                .map("/favicon.ico", "../favicon.ico");

        DocumentAdapter document = loadDocument(this, "headLinkReplacement.html");
        Map<String, String> mapping = linkMapper.findAndReplaceLinks(document, "/test/");
        assertEquals(3, mapping.size());
        assertCorrectMapping(mapping);
        assertCorrectElement(document.select("link[rel=stylesheet]").first(),
                "href", "../assets/style.css");
        assertCorrectElement(document.select("script[src]").first(),
                "src", "../assets/script.js");
        assertCorrectElement(document.select("link[rel=icon]").first(),
                "href", "../favicon.ico");
    }

    @Test
    public void testBodyLinkReplacement() throws Exception {
        linkTranslator.map("/otherPage.html", "../otherPage.html")
                .map("/media/image.jpg", "../media/image.jpg")
                .map("/media/video.mp4", "../media/video.mp4")
                .map("/media/audio.mp3",  "../media/audio.mp3");

        DocumentAdapter document = loadDocument(this, "bodyLinkReplacement.html");
        Map<String, String> mapping = linkMapper.findAndReplaceLinks(document, "/test/");
        assertEquals(4, mapping.size());
        assertCorrectMapping(mapping);
        assertCorrectElement(document.select("a[href]").first(),
                "href", "../otherPage.html",
                "target", "_blank");
        assertCorrectElement(document.select("img[src]").first(),
                "src", "../media/image.jpg",
                "alt", "testImage");
        assertCorrectElement(document.select("video > source").first(),
                "src", "../media/video.mp4",
                "type", "video/mp4");
        assertCorrectElement(document.select("audio > source").first(),
                "src", "../media/audio.mp3",
                "type", "audio/mpeg");
    }

    @Test
    public void testForeignLinkAvoidance() throws Exception {
        linkTranslator.map("/", "../index.html");

        DocumentAdapter document = loadDocument(this, "foreignLinkAvoidance.html");
        Map<String, String> mapping = linkMapper.findAndReplaceLinks(document, "/test/");
        assertEquals(2, mapping.size());
        assertTrue(mapping.containsKey("https://example.com/"));
        assertNull(mapping.remove("https://example.com/"));
        assertCorrectMapping(mapping);

        assertCorrectElement(document.select("#linkRoot").first(),
                "href", "../index.html");
        assertCorrectElement(document.select("#linkForeign").first(),
                "href", "https://example.com/");
    }

    @Test
    public void testDataLinkAvoidance() throws Exception {
        linkTranslator.map("/", "../index.html");

        DocumentAdapter document = loadDocument(this, "dataLinkAvoidance.html");
        Map<String, String> mapping = linkMapper.findAndReplaceLinks(document, "/test/");
        assertEquals(2, mapping.size());
        assertTrue(mapping.containsKey("data:,"));
        assertNull(mapping.remove("data:,"));
        assertCorrectMapping(mapping);

        assertCorrectElement(document.select("#linkRoot").first(),
                "href", "../index.html");
        assertCorrectElement(document.select("#linkData").first(),
                "src", "data:,");
    }

    private void assertCorrectElement(ElementAdapter element, String... attributes) {
        if (attributes.length % 2 != 0) throw new IllegalArgumentException("attributes must be even");
        assertNotNull(element);
        for (int i = 0; i < attributes.length; i += 2) {
            String attribute = element.attr(attributes[i]);
            String expectedValue = attributes[i + 1];
            assertEquals(expectedValue, attribute);
        }
    }

    private void assertCorrectMapping(Map<String, String> mapping) {
        assertEquals(linkTranslator.mapping, mapping);
    }
}
