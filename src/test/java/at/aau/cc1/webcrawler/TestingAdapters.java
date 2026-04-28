package at.aau.cc1.webcrawler;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;
import at.aau.cc1.webcrawler.adapter.HttpStatusExceptionAdapter;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;

public class TestingAdapters {
    public static DocumentAdapter loadDocument(Object caller, String path) throws Exception {
        InputStream stream = caller.getClass().getResourceAsStream(path);
        Document document = Jsoup.parse(stream, "UTF-8", "https://example.org/");
        return new DocumentAdapter(document);
    }

    public static HttpStatusExceptionAdapter makeHttpStatusException(String url, int statusCode) {
        return new HttpStatusExceptionAdapter(new HttpStatusException(null, statusCode, url));
    }
}
