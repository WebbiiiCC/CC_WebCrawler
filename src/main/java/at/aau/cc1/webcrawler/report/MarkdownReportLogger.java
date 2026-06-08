package at.aau.cc1.webcrawler.report;

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

    protected StringBuilder getOutput() {
        return sections.sequencedValues().stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
    }
}
