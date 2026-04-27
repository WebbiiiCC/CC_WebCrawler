package at.aau.cc1.webcrawler.report;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class MarkdownReportLoggerTest {
    private MarkdownReportLogger reportLogger;

    @BeforeEach
    public void setupReportLogger() {
        reportLogger = new MarkdownReportLogger() {
            @Override
            public void finish() {
            }
        };
    }

    @ParameterizedTest
    @ValueSource(strings = {"This is a test message!", "This is another test message!"})
    public void testSimpleMessages(String message) {
        reportLogger.log(message);
        reportLogger.finish();

        String output = reportLogger.getOutput().toString().split("\n")[0];
        assertEquals(message, output.trim());
        assertTrue(output.endsWith("  "));
    }

    @Test
    public void testMultipleMessages() {
        for (int i = 1; i <= 3; i++) {
            reportLogger.log("Test #" + i);
        }
        reportLogger.finish();

        String[] output = reportLogger.getOutput().toString().split("\n");
        for (int i = 1; i <= 3; i++) {
            String line = output[i - 1];
            assertEquals("Test #" + i, line.trim());
            assertTrue(line.endsWith("  "));
        }
    }

    @Test
    public void testSections() {
        for (int i = 1; i <= 3; i++) {
            reportLogger.beginSection("Test Section #" + i, i);
        }
        reportLogger.finish();

        String[] output = reportLogger.getOutput().toString().split("\n");
        for (int i = 1; i <= 2; i++) {
            String line = output[i - 1];
            assertEquals("#".repeat(i) + " Test Section #" + i, line);
        }
    }

    @Test
    public void testSectionText() {
        reportLogger.beginSection("Test Section #1", 1);
        reportLogger.log("Test #1");
        reportLogger.beginSection("Test Section #2", 2);
        reportLogger.log("Test #2");
        reportLogger.log("More text for #2");
        reportLogger.beginSection("Final test section", 1);
        reportLogger.log("Final test text");
        reportLogger.finish();

        String output = reportLogger.getOutput().toString();
        assertEquals("""
                # Test Section #1
                Test #1 \s
                ## Test Section #2
                Test #2 \s
                More text for #2 \s
                # Final test section
                Final test text \s
                """, output);
    }
}
