package at.aau.cc1.webcrawler.fetch;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;
import at.aau.cc1.webcrawler.adapter.HttpStatusExceptionAdapter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static at.aau.cc1.webcrawler.TestingAdapters.makeHttpStatusException;

@RequiredArgsConstructor
public class TestingDocumentFetcher implements DocumentFetcher {
    private final String url;
    private final DocumentAdapter document;

    @Getter
    private int fetches = 0;

    @Getter
    private int errors = 0;

    @Override
    public DocumentAdapter fetchDocument(String url) throws HttpStatusExceptionAdapter {
        if (!this.url.equals(url)) {
            errors++;
            throw makeHttpStatusException(url, 404);
        }
        fetches++;
        return this.document;
    }
}
