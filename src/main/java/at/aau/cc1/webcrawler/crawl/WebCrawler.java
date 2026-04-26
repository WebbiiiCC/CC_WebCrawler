package at.aau.cc1.webcrawler.crawl;

import at.aau.cc1.webcrawler.fetch.DocumentFetcher;
import at.aau.cc1.webcrawler.mapping.LinkMapper;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebCrawler {
    private final DocumentFetcher documentFetcher;
    private final LinkMapper linkMapper;
    private final String baseUrl;

    public WebCrawler(DocumentFetcher documentFetcher, LinkMapper linkMapper, String baseUrl) {
        this.documentFetcher = documentFetcher;
        this.linkMapper = linkMapper;
        this.baseUrl = baseUrl;
    }

    public void downloadPage(String webPath, File contentRoot, int maxDepth) throws IOException {
        validateWebPath(webPath);
        createAndValidateContentRoot(contentRoot);

        Queue<DownloadTask> tasks = new LinkedList<>();
        tasks.add(getInitialDownloadTask(webPath, contentRoot));
        do {
            DownloadTask task = tasks.poll();
            if (task.depth() > maxDepth) {
                System.err.println("Skipping download task for " + task.webPath() + " because the max depth of " + maxDepth + " has been reached");
                continue;
            }

            List<DownloadTask> newTasks = handleDownloadTask(task, contentRoot);
            tasks.addAll(newTasks);
        } while (!tasks.isEmpty());
    }

    private List<DownloadTask> handleDownloadTask(DownloadTask task, File contentRoot) throws IOException {
        File localDestination = task.localDestination();
        if (localDestination.exists()) {
            //This page is already downloaded
            return List.of();
        }

        Document document;
        try {
            document = documentFetcher.fetchDocument(baseUrl + task.webPath());
        } catch (HttpStatusException e) {
            System.err.println("Failed to download " + e.getUrl() + ": HTTP Status Code " + e.getStatusCode());
            return List.of();
        }
        HashMap<String, String> linkMapping = linkMapper.findAndReplaceLinks(document, task.webPath());
        storePageContent(document, localDestination);
        return createNestedDownloadTasks(linkMapping, contentRoot, task.depth());
    }

    private void storePageContent(Document document, File localDestination) throws IOException {
        localDestination.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(localDestination)) {
            fos.write(document.html().getBytes(StandardCharsets.UTF_8));
        }
    }

    private List<DownloadTask> createNestedDownloadTasks(HashMap<String, String> linkMapping, File contentRoot, int currentDepth) {
        List<DownloadTask> tasks = new LinkedList<>();
        for (Map.Entry<String, String> mappedLink : linkMapping.entrySet()) {
            String link = mappedLink.getKey();
            String path = mappedLink.getValue();
            File localDestination = new File(contentRoot, path);
            tasks.add(new DownloadTask(link, localDestination, currentDepth + 1));
        }
        return tasks;
    }

    private DownloadTask getInitialDownloadTask(String webPath, File contentRoot) {
        String localPath = webPath;
        if (localPath.endsWith("/")) {
            localPath += "index.html";
        }
        File initialDestination = new File(contentRoot, localPath);
        return new DownloadTask(webPath, initialDestination, 0);
    }

    private void validateWebPath(String webPath) {
        if (webPath == null) throw new NullPointerException("webPath is null");
        if (!webPath.startsWith("/")) throw new IllegalArgumentException("webPath must start with '/'");
    }

    private void createAndValidateContentRoot(File contentRoot) {
        if (contentRoot == null) {
            throw new NullPointerException("contentRoot is null");
        }
        if (!contentRoot.mkdirs() && !contentRoot.isDirectory()) {
            throw new RuntimeException("contentRoot does not exist and could not be created");
        }
    }
}
