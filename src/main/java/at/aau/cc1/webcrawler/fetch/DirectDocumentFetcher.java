package at.aau.cc1.webcrawler.fetch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class DirectDocumentFetcher implements DocumentFetcher {
    @Override
    public Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}
