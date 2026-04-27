package at.aau.cc1.webcrawler.crawl;

import at.aau.cc1.webcrawler.report.MarkdownReportLogger;
import lombok.Getter;

@Getter
public class TestingReportLogger extends MarkdownReportLogger {
    private boolean finished = false;
    private boolean invocationsPastFinish = false;

    @Override
    public void beginSection(String sectionName, int headingDepth) {
        if (finished) invocationsPastFinish = true;
        super.beginSection(sectionName, headingDepth);
    }

    @Override
    public void log(String message) {
        if (finished) invocationsPastFinish = true;
        super.log(message);
    }

    @Override
    public void finish() {
        finished = true;
    }

    @Override
    public StringBuilder getOutput() {
        return super.getOutput();
    }
}
