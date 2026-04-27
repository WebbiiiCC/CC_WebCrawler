package at.aau.cc1.webcrawler.storage;

import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LocalStorageTarget implements StorageTarget {
    @Override
    public void store(Document document, File path) throws IOException {
        path.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(document.html().getBytes(StandardCharsets.UTF_8));
        }
    }
}
