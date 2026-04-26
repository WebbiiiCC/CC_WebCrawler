package at.aau.cc1.webcrawler.mapping.translate;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class LocalLinkTranslator implements LinkTranslator {
    private final String baseUrl;

    public LocalLinkTranslator(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String translateLink(String currentPath, String link) throws MalformedURLException {
        URI uri = URI.create(link);
        if (isForeignHost(uri)) {
            return null;
        }
        String path = uri.getPath();
        if (path.startsWith("/")) {
            return translateAbsoluteLink(currentPath, path);
        } else {
            return translateRelativeLink(path);
        }
    }

    private String translateAbsoluteLink(String currentPath, String link) {
        int firstDifference = findFirstPathDifference(currentPath, link);
        if (firstDifference == -1) {
            return link.substring(1);
        }
        int distance = countPathSeparators(currentPath.substring(firstDifference));
        String relativePath = "../".repeat(distance) + link.substring(firstDifference);
        return translateRelativeLink(relativePath);
    }

    private String translateRelativeLink(String link) {
        if (link.endsWith("/")) {
            link += "index.html";
        }
        return link;
    }

    private int countPathSeparators(String str) {
        int count = 0;
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) == '/') {
                ++count;
            }
        }
        return count;
    }

    private int findFirstPathDifference(String path1, String path2) {
        path1 = stripFileName(path1);
        path2 = stripFileName(path2);

        if (path1.equals(path2)) {
            return -1;
        }

        int shorterLength = Math.min(path1.length(), path2.length());
        for (int i = 0; i < shorterLength; i++) {
            if (path1.charAt(i) != path2.charAt(i)) {
                return path1.lastIndexOf('/', i) + 1;
            }
        }
        return shorterLength;
    }

    private String stripFileName(String path) {
        int idx = path.lastIndexOf('/');
        return path.substring(0, idx + 1);
    }

    private boolean isForeignHost(URI uri) throws MalformedURLException {
        if (uri.isAbsolute()) {
            URL url = uri.toURL();
            String baseUrl = url.getProtocol() + "://" + url.getAuthority();
            return !this.baseUrl.equals(baseUrl);
        }
        return false;
    }
}
