package at.aau.cc1.webcrawler.adapter;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;

@RequiredArgsConstructor
public class ElementAdapter {
    private final Element innerElement;

    public String attr(String attributeKey) {
        return innerElement.attr(attributeKey);
    }

    public void attr(String attributeKey, String attributeValue) {
        innerElement.attr(attributeKey, attributeValue);
    }

    public String tagName() {
        return innerElement.tagName();
    }

    public String innerText() {
        return innerElement.text();
    }
}
