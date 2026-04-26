package at.aau.cc1.webcrawler.fetch;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface DocumentFetcher {
    Document fetchDocument(String url) throws IOException;
}
