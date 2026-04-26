package at.aau.cc1.webcrawler.mapping.translate;

import java.net.MalformedURLException;

public interface LinkTranslator {
    String translateLink(String currentPath, String link) throws MalformedURLException;
}
