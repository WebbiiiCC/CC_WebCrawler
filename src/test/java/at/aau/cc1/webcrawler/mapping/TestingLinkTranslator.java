package at.aau.cc1.webcrawler.mapping;

import at.aau.cc1.webcrawler.mapping.translate.LinkTranslator;

import java.util.HashMap;
import java.util.Map;

class TestingLinkTranslator implements LinkTranslator {
    final Map<String, String> mapping = new HashMap<>();

    TestingLinkTranslator map(String link, String result) {
        mapping.put(link, result);
        return this;
    }

    void reset() {
        mapping.clear();
    }

    @Override
    public String translateLink(String currentPath, String link) {
        return mapping.get(link);
    }
}
