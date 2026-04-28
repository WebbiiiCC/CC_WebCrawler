package at.aau.cc1.webcrawler.adapter;

import lombok.RequiredArgsConstructor;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

@RequiredArgsConstructor
public class DocumentAdapter {
    private final Document innerDocument;

    public ElementsAdapter select(String cssQuery) {
        return new ElementsAdapter(innerDocument.select(cssQuery));
    }

    public void truncateBody() {
        innerDocument.body().remove();
    }

    public String asHtml() {
        return innerDocument.html();
    }

    public String title() {
        return innerDocument.title();
    }

    public static DocumentAdapter fetchFromUrl(String url) throws IOException {
        try {
            return new DocumentAdapter(Jsoup.connect(url).get());
        } catch (HttpStatusException httpStatusException) {
            throw new HttpStatusExceptionAdapter(httpStatusException);
        }
    }
}
