package at.aau.cc1.webcrawler.report;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;

public interface ReportLogger {
    void beginSection(String sectionName, int headingDepth);
    void log(String message);
    void recordDocument(DocumentAdapter document, String path);
    void finish();
}
