package at.aau.cc1.webcrawler.mapping;

import org.jsoup.nodes.Document;

import java.util.HashMap;

public interface LinkMapper {
    HashMap<String, String> findAndReplaceLinks(Document document, String currentPath);
}
