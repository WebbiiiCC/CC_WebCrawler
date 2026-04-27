package at.aau.cc1.webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;

public class DocumentInclusion {
    public static Document loadDocument(Object caller, String path) throws Exception {
        InputStream stream = caller.getClass().getResourceAsStream(path);
        return Jsoup.parse(stream, "UTF-8", "https://example.org/");
    }
}
