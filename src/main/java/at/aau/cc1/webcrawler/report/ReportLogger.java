package at.aau.cc1.webcrawler.report;

public interface ReportLogger {
    void beginSection(String sectionName, int headingDepth);
    void log(String message);
    void finish();
}
