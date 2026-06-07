package at.aau.cc1.webcrawler.storage;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;

import java.io.File;

public class DiscardingStorageTarget implements StorageTarget {
    @Override
    public void store(DocumentAdapter document, File path) {
    }
}
