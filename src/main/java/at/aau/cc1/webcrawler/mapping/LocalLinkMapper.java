package at.aau.cc1.webcrawler.mapping;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;
import at.aau.cc1.webcrawler.adapter.ElementAdapter;
import at.aau.cc1.webcrawler.adapter.ElementsAdapter;
import at.aau.cc1.webcrawler.mapping.translate.LinkTranslator;
import lombok.RequiredArgsConstructor;

import java.net.MalformedURLException;
import java.util.HashMap;

@RequiredArgsConstructor
public class LocalLinkMapper implements LinkMapper {
    private final LinkTranslator linkTranslator;

    @Override
    public HashMap<String, String> findAndReplaceLinks(DocumentAdapter document, String currentPath) {
        // Disclaimer: This method is based on the example found at https://jsoup.org/cookbook/extracting-data/example-list-links
        // Last accessed: 2026-04-26 16:50
        HashMap<String, String> linkMap = new HashMap<>();
        linkMap.putAll(this.replaceElementLinks(document.select("a[href]"), "href", currentPath));
        linkMap.putAll(this.replaceElementLinks(document.select("[src]"), "src", currentPath));
        linkMap.putAll(this.replaceElementLinks(document.select("link[href]"), "href", currentPath));
        return linkMap;
    }

    private HashMap<String, String> replaceElementLinks(ElementsAdapter elements, String attributeName, String currentPath) {
        HashMap<String, String> linkMapping = new HashMap<>();
        for (ElementAdapter element : elements.getElements()) {
            String link = element.attr(attributeName);
            try {
                String localPath = this.linkTranslator.translateLink(currentPath, link);
                if (localPath != null) {
                    element.attr(attributeName, localPath);
                    linkMapping.put(link, localPath);
                } else {
                    linkMapping.put(link, null);
                }
            } catch (MalformedURLException e) {
                System.err.println("Can't find local path for link: " + link);
            }
        }
        return linkMapping;
    }
}
