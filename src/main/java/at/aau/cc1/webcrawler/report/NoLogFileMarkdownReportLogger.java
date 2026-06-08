package at.aau.cc1.webcrawler.report;

import java.io.File;

public class NoLogFileMarkdownReportLogger extends FileMarkdownReportLogger {
    public NoLogFileMarkdownReportLogger(File destinationFile) {
        super(destinationFile);
    }

    @Override
    public void log(String sectionKey, String message) {
    }
}
