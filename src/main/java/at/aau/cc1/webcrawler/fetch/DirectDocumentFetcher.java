package at.aau.cc1.webcrawler.fetch;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;

import java.io.IOException;

public class DirectDocumentFetcher implements DocumentFetcher {
    @Override
    public DocumentAdapter fetchDocument(String url) throws IOException {
        return DocumentAdapter.fetchFromUrl(url);
    }
}
