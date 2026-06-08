package at.aau.cc1.webcrawler.report;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;
import at.aau.cc1.webcrawler.adapter.ElementAdapter;
import at.aau.cc1.webcrawler.adapter.ElementsAdapter;

import java.util.LinkedHashMap;
import java.util.SequencedMap;

public abstract class MarkdownReportLogger implements ReportLogger {
    private final SequencedMap<String, StringBuilder> sections = new LinkedHashMap<>();

    @Override
    public void beginSection(String sectionKey, String sectionName, int headingDepth) {
        StringBuilder sectionText = sections.getOrDefault(sectionKey, new StringBuilder());
        sectionText.append("#".repeat(headingDepth)).append(' ').append(sectionName).append("\n");
        sections.put(sectionKey, sectionText);
    }

    @Override
    public void log(String sectionKey, String message) {
        sections.get(sectionKey).append(message).append("  \n");
    }

    @Override
    public void recordDocument(String sectionKey, DocumentAdapter document, String path) {
        ElementsAdapter headings = document.select("h1, h2, h3, h4, h5, h6");
        for (ElementAdapter heading : headings.getElements()) {
            int headingSize = Integer.parseUnsignedInt(heading.tagName().substring(1));

            // Use a heading 2 sizes smaller to avoid visual conflict with the
            // other headers used to indicate which page is being downloaded
            beginSection(sectionKey + " document", heading.innerText(), headingSize + 2);
        }
    }

    protected StringBuilder getOutput() {
        return sections.sequencedValues().stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
    }
}
