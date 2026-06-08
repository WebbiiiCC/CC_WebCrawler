package at.aau.cc1.webcrawler.report;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrintingReportLoggerTest {
    private ByteArrayOutputStream arrayStream;
    private PrintStream printStream;
    private PrintingReportLogger reportLogger;

    @BeforeEach
    public void setupReportLogger() {
        arrayStream = new ByteArrayOutputStream();
        printStream = new PrintStream(arrayStream);
        reportLogger = new PrintingReportLogger("Unit Test", printStream);
    }

    @AfterEach
    public void cleanupStreams() throws IOException {
        arrayStream.close();
        printStream.close();
    }

    @Test
    public void testSimpleMessages() {
        reportLogger.beginSection("sec", "Test Section", 1);
        reportLogger.log("sec", "This is a test message!");
        reportLogger.finish();

        String output = arrayStream.toString();
        assertEquals("""
                [Unit Test] # Test Section
                [Unit Test] This is a test message!
                [Unit Test] Crawl finished!
                """, output);
    }
}
