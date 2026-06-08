package at.aau.cc1.webcrawler.report;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;

public interface ReportLogger {
    void beginSection(String sectionKey, String sectionName, int headingDepth);
    void log(String sectionKey, String message);
    void recordDocument(String sectionKey, DocumentAdapter document, String path);
    void finish();
}
