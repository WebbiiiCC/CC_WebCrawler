package at.aau.cc1.webcrawler.storage;

import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public interface StorageTarget {
    void store(Document document, File path) throws IOException;
}
