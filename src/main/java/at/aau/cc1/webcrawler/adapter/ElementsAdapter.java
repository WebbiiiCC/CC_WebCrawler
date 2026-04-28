package at.aau.cc1.webcrawler.adapter;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

@RequiredArgsConstructor
public class ElementsAdapter {
    private final Elements innerElements;

    public List<ElementAdapter> getElements() {
        return innerElements.stream().map(ElementAdapter::new).toList();
    }

    public ElementAdapter first() {
        Element element = innerElements.first();
        if (element != null) {
            return new ElementAdapter(element);
        }
        return null;
    }

    public boolean isEmpty() {
        return innerElements.isEmpty();
    }
}
