package at.aau.cc1.webcrawler.fetch;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class HeadOnlyDocumentFetcher implements DocumentFetcher {
    private final DocumentFetcher documentFetcher;

    @Override
    public DocumentAdapter fetchDocument(String url) throws IOException {
        DocumentAdapter document = documentFetcher.fetchDocument(url);
        document.truncateBody();
        return document;
    }
}
