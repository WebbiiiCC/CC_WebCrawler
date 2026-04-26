package at.aau.cc1.webcrawler.mapping;

import at.aau.cc1.webcrawler.mapping.translate.LinkTranslator;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.util.HashMap;

public class LocalLinkMapper implements LinkMapper {
    private final LinkTranslator linkTranslator;

    public LocalLinkMapper(LinkTranslator linkTranslator) {
        this.linkTranslator = linkTranslator;
    }

    @Override
    public HashMap<String, String> findAndReplaceLinks(Document document, String currentPath) {
        // Disclaimer: This method is based on the example found at https://jsoup.org/cookbook/extracting-data/example-list-links
        // Last accessed: 2026-04-26 16:50
        HashMap<String, String> linkMap = new HashMap<>();
        linkMap.putAll(this.replaceElementLinks(document.select("a[href]"), "href", currentPath));
        linkMap.putAll(this.replaceElementLinks(document.select("[src]"), "src", currentPath));
        linkMap.putAll(this.replaceElementLinks(document.select("link[href]"), "href", currentPath));
        return linkMap;
    }

    private HashMap<String, String> replaceElementLinks(Elements elements, String attributeName, String currentPath) {
        HashMap<String, String> linkMapping = new HashMap<>();
        for (Element element : elements) {
            String link = element.attr(attributeName);
            try {
                String localPath = this.linkTranslator.translateLink(currentPath, link);
                if (localPath != null) {
                    element.attr(attributeName, localPath);
                    linkMapping.put(link, localPath);
                }
            } catch (MalformedURLException e) {
                System.err.println("Can't find local path for link: " + link);
            }
        }
        return linkMapping;
    }
}
