package at.aau.cc1.webcrawler.fetch;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

@RequiredArgsConstructor
public class TestingDocumentFetcher implements DocumentFetcher {
    private final String url;
    private final Document document;

    @Getter
    private int fetches = 0;

    @Getter
    private int errors = 0;

    @Override
    public Document fetchDocument(String url) throws HttpStatusException {
        if (!this.url.equals(url)) {
            errors++;
            throw new HttpStatusException(null, 404, url);
        }
        fetches++;
        return this.document;
    }
}
