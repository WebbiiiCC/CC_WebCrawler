package at.aau.cc1.webcrawler.report;

public abstract class MarkdownReportLogger implements ReportLogger {
    private final StringBuilder output = new StringBuilder();

    @Override
    public void beginSection(String sectionName, int headingDepth) {
        output.append("#".repeat(headingDepth)).append(' ').append(sectionName).append("\n");
    }

    @Override
    public void log(String message) {
        output.append(message).append("  \n");
    }

    protected StringBuilder getOutput() {
        return output;
    }
}
