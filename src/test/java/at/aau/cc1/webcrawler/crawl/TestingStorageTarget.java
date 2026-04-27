package at.aau.cc1.webcrawler.crawl;

import at.aau.cc1.webcrawler.storage.StorageTarget;
import lombok.Getter;
import org.jsoup.nodes.Document;

import java.io.File;

@Getter
public class TestingStorageTarget implements StorageTarget {
    private Document storedDocument;
    private String storedPath;

    @Override
    public void store(Document document, File path) {
        this.storedDocument = document;
        this.storedPath = path.getPath();
    }
}
