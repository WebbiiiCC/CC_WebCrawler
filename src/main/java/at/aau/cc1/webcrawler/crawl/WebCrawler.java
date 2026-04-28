package at.aau.cc1.webcrawler.crawl;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;
import at.aau.cc1.webcrawler.adapter.HttpStatusExceptionAdapter;
import at.aau.cc1.webcrawler.fetch.DocumentFetcher;
import at.aau.cc1.webcrawler.mapping.LinkMapper;
import at.aau.cc1.webcrawler.report.ReportLogger;
import at.aau.cc1.webcrawler.storage.StorageTarget;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
public class WebCrawler {
    private final DocumentFetcher documentFetcher;
    private final ReportLogger reportLogger;
    private final LinkMapper linkMapper;
    private final StorageTarget storageTarget;
    private final String baseUrl;

    public void downloadPage(String webPath, File contentRoot, int maxDepth) throws IOException {
        validateWebPath(webPath);
        createAndValidateContentRoot(contentRoot);

        reportLogger.beginSection("Crawl: " + baseUrl + webPath, 1);

        Queue<DownloadTask> tasks = new LinkedList<>();
        tasks.add(getInitialDownloadTask(webPath, contentRoot));
        do {
            DownloadTask task = tasks.poll();
            if (task.depth() > maxDepth) {
                reportLogger.log("Skipping download of " + task.webPath() + " because the max depth of " + maxDepth + " has been reached");
                continue;
            }

            List<DownloadTask> newTasks = handleDownloadTask(task, contentRoot);
            tasks.addAll(newTasks);
        } while (!tasks.isEmpty());

        reportLogger.finish();
    }

    private List<DownloadTask> handleDownloadTask(DownloadTask task, File contentRoot) throws IOException {
        File localDestination = task.localDestination();
        if (localDestination.exists()) {
            reportLogger.log("Skipping download of " + task.webPath() + " because it was already downloaded before");
            return List.of();
        }

        reportLogger.beginSection(task.webPath(), task.depth() + 2);
        reportLogger.log("Depth: " + task.depth());

        DocumentAdapter document;
        try {
            document = documentFetcher.fetchDocument(baseUrl + task.webPath());
        } catch (HttpStatusExceptionAdapter e) {
            reportLogger.log("Error fetching " + e.getUrl() + ": HTTP Status Code " + e.getStatusCode());
            reportLogger.log("This link will stay broken in the local page!");
            return List.of();
        }
        HashMap<String, String> linkMapping = linkMapper.findAndReplaceLinks(document, task.webPath());
        storageTarget.store(document, localDestination);
        return createNestedDownloadTasks(linkMapping, contentRoot, task.depth());
    }

    private List<DownloadTask> createNestedDownloadTasks(HashMap<String, String> linkMapping, File contentRoot, int currentDepth) {
        List<DownloadTask> tasks = new LinkedList<>();
        for (Map.Entry<String, String> mappedLink : linkMapping.entrySet()) {
            String link = mappedLink.getKey();
            String path = mappedLink.getValue();
            if (path != null) {
                reportLogger.log("Link to path: **" + link + "** (rewritten: **" + path + "**)");
                File localDestination = new File(contentRoot, path);
                tasks.add(new DownloadTask(link, localDestination, currentDepth + 1));
            } else {
                reportLogger.log("Link to external page: **" + link + "**");
            }
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
