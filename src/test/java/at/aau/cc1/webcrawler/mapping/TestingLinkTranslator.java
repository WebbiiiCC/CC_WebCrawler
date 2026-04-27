package at.aau.cc1.webcrawler.mapping;

import at.aau.cc1.webcrawler.mapping.translate.LinkTranslator;

import java.util.HashMap;
import java.util.Map;

public class TestingLinkTranslator implements LinkTranslator {
    final Map<String, String> mapping = new HashMap<>();

    public TestingLinkTranslator map(String link, String result) {
        mapping.put(link, result);
        return this;
    }

    public void reset() {
        mapping.clear();
    }

    @Override
    public String translateLink(String currentPath, String link) {
        return mapping.get(link);
    }
}
