package at.aau.cc1.webcrawler.fetch;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;

import java.io.IOException;

@RequiredArgsConstructor
public class HeadOnlyDocumentFetcher implements DocumentFetcher {
    private final DocumentFetcher documentFetcher;

    @Override
    public Document fetchDocument(String url) throws IOException {
        Document document = documentFetcher.fetchDocument(url);
        document.body().remove();
        return document;
    }
}
