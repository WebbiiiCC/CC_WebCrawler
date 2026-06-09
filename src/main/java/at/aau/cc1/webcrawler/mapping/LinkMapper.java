package at.aau.cc1.webcrawler.mapping;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;

import java.net.MalformedURLException;
import java.util.HashMap;

public interface LinkMapper {
    HashMap<String, String> findAndReplaceLinks(DocumentAdapter document, String currentPath) throws MalformedURLException;
}
