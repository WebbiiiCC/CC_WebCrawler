package at.aau.cc1.webcrawler.crawl;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;
import at.aau.cc1.webcrawler.storage.StorageTarget;
import lombok.Getter;

import java.io.File;

@Getter
public class TestingStorageTarget implements StorageTarget {
    private DocumentAdapter storedDocument;
    private String storedPath;

    @Override
    public void store(DocumentAdapter document, File path) {
        this.storedDocument = document;
        this.storedPath = path.getPath();
    }
}
