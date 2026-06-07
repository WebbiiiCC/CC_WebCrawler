package at.aau.cc1.webcrawler.storage;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static at.aau.cc1.webcrawler.TestingAdapters.loadDocument;
import static org.junit.jupiter.api.Assertions.*;

public class LocalStorageTargetTest {
    private DocumentAdapter document;
    private LocalStorageTarget localStorageTarget;
    private File simplePath;
    private File nestedPath;

    @BeforeEach
    public void setupLocalStorageTarget() throws Exception {
        document = loadDocument(this, "simpleDocument.html");
        localStorageTarget = new LocalStorageTarget();
        simplePath = new File("/tmp/CC_WebCrawler_Test.html");
        nestedPath = new File("/tmp/CC_WebCrawler/Test/test.html");
    }

    @AfterEach
    public void cleanupLocalStorage() {
        simplePath.delete();
        nestedPath.delete();
    }

    @Test
    public void testSimplePathFileCreation() {
        assertDoesNotThrow(() -> localStorageTarget.store(document, simplePath));
        assertTrue(simplePath.exists());
        assertTrue(simplePath.isFile());
    }

    @Test
    public void testNestedPathFileCreation() {
        assertDoesNotThrow(() -> localStorageTarget.store(document, nestedPath));
        assertTrue(nestedPath.exists());
        assertTrue(nestedPath.isFile());
    }

    @Test
    public void testFileEncodingUtf8() {
        assertDoesNotThrow(() -> localStorageTarget.store(document, simplePath));

        CharsetDecoder utf8Decoder = StandardCharsets.UTF_8.newDecoder();
        assertDoesNotThrow(() -> {
            byte[] fileBytes = Files.readAllBytes(simplePath.toPath());
            utf8Decoder.decode(ByteBuffer.wrap(fileBytes));
        });
    }
}
