package at.aau.cc1.webcrawler.report;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;
import at.aau.cc1.webcrawler.adapter.ElementAdapter;
import at.aau.cc1.webcrawler.adapter.ElementsAdapter;

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

    @Override
    public void recordDocument(DocumentAdapter document, String path) {
        ElementsAdapter headings = document.select("h1, h2, h3, h4, h5, h6");
        for (ElementAdapter heading : headings.getElements()) {
            int headingSize = Integer.parseUnsignedInt(heading.tagName().substring(1));

            // Use a heading 2 sizes smaller to avoid visual conflict with the
            // other headers used to indicate which page is being downloaded
            beginSection(heading.innerText(), headingSize + 2);
        }
    }

    protected StringBuilder getOutput() {
        return output;
    }
}
