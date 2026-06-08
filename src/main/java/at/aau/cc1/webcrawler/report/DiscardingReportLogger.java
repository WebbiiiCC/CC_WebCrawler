package at.aau.cc1.webcrawler.report;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;

public class DiscardingReportLogger implements ReportLogger {
    @Override
    public void beginSection(String sectionKey, String sectionName, int headingDepth) {
    }

    @Override
    public void log(String sectionKey, String message) {
    }

    @Override
    public void recordDocument(String sectionKey, DocumentAdapter document, String path) {
    }

    @Override
    public void finish() {
    }
}
