package at.aau.cc1.webcrawler.report;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class FileMarkdownReportLogger extends MarkdownReportLogger {
    private final File destinationFile;

    @Override
    public void finish() throws IOException {
        String markdownText = super.getOutput().toString();

        try (FileOutputStream fileStream = new FileOutputStream(destinationFile, true)) {
            fileStream.write(markdownText.getBytes(StandardCharsets.UTF_8));
            fileStream.flush();
        }
    }
}
