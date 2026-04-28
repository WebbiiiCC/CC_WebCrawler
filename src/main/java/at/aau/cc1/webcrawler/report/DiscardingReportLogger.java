package at.aau.cc1.webcrawler.report;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;

public class DiscardingReportLogger implements ReportLogger {
    @Override
    public void beginSection(String sectionName, int headingDepth) {
    }

    @Override
    public void log(String message) {
    }

    @Override
    public void recordDocument(DocumentAdapter document, String path) {
    }

    @Override
    public void finish() {
    }
}
