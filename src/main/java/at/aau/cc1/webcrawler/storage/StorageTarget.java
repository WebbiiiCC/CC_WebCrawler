package at.aau.cc1.webcrawler.storage;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;

import java.io.File;
import java.io.IOException;

public interface StorageTarget {
    void store(DocumentAdapter document, File path) throws IOException;
}
