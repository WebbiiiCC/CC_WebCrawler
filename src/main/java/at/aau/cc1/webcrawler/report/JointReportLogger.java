package at.aau.cc1.webcrawler.report;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class JointReportLogger implements ReportLogger {
    private final ReportLogger first, second;

    @Override
    public void beginSection(String sectionKey, String sectionName, int headingDepth) {
        first.beginSection(sectionKey, sectionName, headingDepth);
        second.beginSection(sectionKey, sectionName, headingDepth);
    }

    @Override
    public void log(String sectionKey, String message) {
        first.log(sectionKey, message);
        second.log(sectionKey, message);
    }

    @Override
    public void finish() throws IOException {
        first.finish();
        second.finish();
    }
}
