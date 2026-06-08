package at.aau.cc1.webcrawler.report;

import lombok.RequiredArgsConstructor;

import java.io.PrintStream;

@RequiredArgsConstructor
public class PrintingReportLogger implements ReportLogger {
    private final String tag;
    private final PrintStream out;

    @Override
    public void beginSection(String sectionKey, String sectionName, int headingDepth) {
        out.println("[" + tag + "] " + "#".repeat(headingDepth) + " " + sectionName);
    }

    @Override
    public void log(String sectionKey, String message) {
        out.println("[" + tag + "] " + message);
    }

    @Override
    public void finish() {
        out.println("[" + tag + "] Crawl finished!");
    }
}
