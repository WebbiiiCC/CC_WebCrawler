package at.aau.cc1.webcrawler.storage;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LocalStorageTarget implements StorageTarget {
    @Override
    public void store(DocumentAdapter document, File path) throws IOException {
        if (!path.getParentFile().exists() && !path.getParentFile().mkdirs()) {
            throw new FileNotCreatedException("Unable to create directory: " + path.getParentFile().getAbsolutePath());
        }
        try (FileOutputStream fileOutput = new FileOutputStream(path)) {
            fileOutput.write(document.asHtml().getBytes(StandardCharsets.UTF_8));
        }
    }
}
