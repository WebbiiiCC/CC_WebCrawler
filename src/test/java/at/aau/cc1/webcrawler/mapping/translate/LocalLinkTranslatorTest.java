package at.aau.cc1.webcrawler.mapping.translate;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalLinkTranslatorTest {
    private final LinkTranslator linkTranslator = new LocalLinkTranslator("https://example.org");

    @ParameterizedTest
    @CsvSource(value = {
            "/;/thePath;thePath",
            "/subfolder/page.html;/image.jpg;../image.jpg",
            "/subfolder/;/image.jpg;../image.jpg",
            "/subfolder/anyFolder/;/subfolder/anotherFolder/image.jpg;../anotherFolder/image.jpg",
            "/subfolder/anyFolder/test.html;/subfolder/anotherFolder/image.jpg;../anotherFolder/image.jpg",
            "/subfolder/page.html;https://example.org/anotherFolder/;../anotherFolder/index.html"
    }, delimiter = ';')
    void testTranslateAbsoluteLinks(String currentPath, String link, String expectedLocalPath) throws MalformedURLException {
        String localPath = linkTranslator.translateLink(currentPath, link);
        assertEquals(expectedLocalPath, localPath);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "/;thePath;thePath",
            "/subfolder/;image.jpg;image.jpg",
            "/subfolder/;../;../index.html"
    }, delimiter = ';')
    void testTranslateRelativeLinks(String currentPath, String link, String expectedLocalPath) throws MalformedURLException {
        String localPath = linkTranslator.translateLink(currentPath, link);
        assertEquals(expectedLocalPath, localPath);
    }
}
