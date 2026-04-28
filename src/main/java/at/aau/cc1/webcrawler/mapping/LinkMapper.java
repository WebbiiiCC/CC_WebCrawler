package at.aau.cc1.webcrawler.mapping;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;

import java.util.HashMap;

public interface LinkMapper {
    HashMap<String, String> findAndReplaceLinks(DocumentAdapter document, String currentPath);
}
