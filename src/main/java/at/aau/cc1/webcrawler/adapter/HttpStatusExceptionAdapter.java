package at.aau.cc1.webcrawler.adapter;

import org.jsoup.HttpStatusException;

import java.io.IOException;

public class HttpStatusExceptionAdapter extends IOException {
    private final HttpStatusException innerException;

    public HttpStatusExceptionAdapter(HttpStatusException innerException) {
        super(innerException);

        this.innerException = innerException;
    }

    public String getUrl() {
        return innerException.getUrl();
    }

    public int getStatusCode() {
        return innerException.getStatusCode();
    }

}
