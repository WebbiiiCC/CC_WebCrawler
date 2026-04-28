package at.aau.cc1.webcrawler.fetch;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;

import java.io.IOException;

public interface DocumentFetcher {
    DocumentAdapter fetchDocument(String url) throws IOException;
}
