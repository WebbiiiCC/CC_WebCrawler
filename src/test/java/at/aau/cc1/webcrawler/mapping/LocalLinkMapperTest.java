package at.aau.cc1.webcrawler.mapping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        Document document = loadDocument("headLinkReplacement.html");
        Map<String, String> mapping = linkMapper.findAndReplaceLinks(document, "/test/");
        assertEquals(3, mapping.size());
        assertCorrectMapping(mapping);

        assertEquals("../assets/style.css", document.select("link[rel=stylesheet]").first().attr("href"));
        assertEquals("../assets/script.js", document.select("script[src]").first().attr("src"));
        assertEquals("../favicon.ico", document.select("link[rel=icon]").first().attr("href"));
    }

    @Test
    public void testBodyLinkReplacement() throws Exception {
        linkTranslator.map("/otherPage.html", "../otherPage.html")
                .map("/media/image.jpg", "../media/image.jpg")
                .map("/media/video.mp4", "../media/video.mp4")
                .map("/media/audio.mp3",  "../media/audio.mp3");

        Document document = loadDocument("bodyLinkReplacement.html");
        Map<String, String> mapping = linkMapper.findAndReplaceLinks(document, "/test/");
        assertEquals(4, mapping.size());
        assertCorrectMapping(mapping);

        Element link = document.select("a[href]").first();
        Element image = document.select("img[src]").first();
        Element video = document.select("video > source").first();
        Element audio = document.select("audio > source").first();

        assertEquals("../otherPage.html", link.attr("href"));
        assertEquals("../media/image.jpg", image.attr("src"));
        assertEquals("../media/video.mp4", video.attr("src"));
        assertEquals("../media/audio.mp3", audio.attr("src"));

        assertEquals("_blank", link.attr("target"));
        assertEquals("testImage", image.attr("alt"));
        assertEquals("video/mp4", video.attr("type"));
        assertEquals("audio/mpeg", audio.attr("type"));
    }

    @Test
    public void testForeignLinkAvoidance() throws Exception {
        linkTranslator.map("/", "../index.html");

        Document document = loadDocument("foreignLinkAvoidance.html");
        Map<String, String> mapping = linkMapper.findAndReplaceLinks(document, "/test/");
        assertEquals(1, mapping.size());
        assertCorrectMapping(mapping);

        assertEquals("../index.html", document.select("#linkRoot").first().attr("href"));
        assertEquals("https://example.com/", document.select("#linkForeign").first().attr("href"));
    }
    private Document loadDocument(String path) throws Exception {
        InputStream stream = getClass().getResourceAsStream(path);
        return Jsoup.parse(stream, "UTF-8", "https://example.org/");
    }

    private void assertCorrectMapping(Map<String, String> mapping) {
        assertEquals(linkTranslator.mapping, mapping);
    }
}
