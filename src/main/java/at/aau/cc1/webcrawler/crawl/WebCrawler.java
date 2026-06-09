package at.aau.cc1.webcrawler.crawl;

import at.aau.cc1.webcrawler.adapter.DocumentAdapter;
import at.aau.cc1.webcrawler.adapter.ElementAdapter;
import at.aau.cc1.webcrawler.adapter.ElementsAdapter;
import at.aau.cc1.webcrawler.fetch.DocumentFetcher;
import at.aau.cc1.webcrawler.mapping.LinkMapper;
import at.aau.cc1.webcrawler.report.ReportLogger;
import at.aau.cc1.webcrawler.storage.FileNotCreatedException;
import at.aau.cc1.webcrawler.storage.StorageTarget;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class WebCrawler {
    private final ExecutorService executor;
    private final DocumentFetcher documentFetcher;
    private final ReportLogger reportLogger;
    private final LinkMapper linkMapper;
    private final StorageTarget storageTarget;
    private final String baseUrl;

    private final AtomicInteger pendingTasks = new AtomicInteger(0);
    private final CompletableFuture<Void> future = new CompletableFuture<>();

    public CompletableFuture<Void> downloadPage(String webPath, File contentRoot, int maxDepth) throws IOException {
        validateWebPath(webPath);
        createAndValidateContentRoot(contentRoot);

        reportLogger.beginSection("crawl", "Crawl: " + baseUrl + webPath, 1);

        DownloadTask initialTask = getInitialDownloadTask(webPath, contentRoot);
        scheduleDownloadTask(initialTask, contentRoot, maxDepth);

        return future;
    }

    private void shutdownCrawler() {
        executor.shutdown();
        try {
            reportLogger.finish();
            future.complete(null);
        } catch (IOException e) {
            future.completeExceptionally(e);
        }
    }

    private void scheduleAndTrackExecution(Runnable executable) {
        synchronized (pendingTasks) {
            pendingTasks.getAndIncrement();
        }

        executor.submit(() -> {
            try {
                executable.run();
            } finally {
                synchronized (pendingTasks) {
                    if (pendingTasks.decrementAndGet() == 0) {
                        shutdownCrawler();
                    }
                }
            }
        });
    }

    private void scheduleDownloadTask(DownloadTask task, File contentRoot, int maxDepth) {
        scheduleAndTrackExecution(() -> {
            try {
                List<DownloadTask> newTasks = runDownloadTask(task, contentRoot);
                for (DownloadTask newTask : newTasks) {
                    if (newTask.depth() > maxDepth) {
                        reportLogger.log(task.webPath(), "Not downloading " + task.webPath() + " because max depth was reached");
                        continue;
                    }
                    scheduleDownloadTask(newTask, contentRoot, maxDepth);
                }
            } catch (Exception e) {
                reportLogger.log(task.webPath(), "Failed to download " + task.webPath() + ": " + e.getMessage());
            }
        });
    }

    private List<DownloadTask> runDownloadTask(DownloadTask task, File contentRoot) throws IOException {
        String webPath = task.webPath();
        reportLogger.beginSection(webPath, webPath, 2);
        reportLogger.log(webPath, "Depth: " + task.depth());

        File localDestination = task.localDestination();
        if (localDestination.exists()) {
            reportLogger.log(webPath, "Skipping download of " + task.webPath() + " because it was already downloaded before");
            return List.of();
        }

        DocumentAdapter document = documentFetcher.fetchDocument(baseUrl + webPath);
        handleDocument(task, document);

        HashMap<String, String> linkMapping = linkMapper.findAndReplaceLinks(document, task.webPath());
        return createNestedDownloadTasks(linkMapping, contentRoot, task.depth());
    }

    private void handleDocument(DownloadTask task, DocumentAdapter document) throws IOException {
        reportDocument(task.webPath(), document);
        storageTarget.store(document, task.localDestination());
    }

    private void reportDocument(String webPath, DocumentAdapter document) {
        ElementsAdapter headings = document.select("h1, h2, h3, h4, h5, h6");
        for (ElementAdapter heading : headings.getElements()) {
            int headingSize = Integer.parseUnsignedInt(heading.tagName().substring(1));

            // Use a heading 2 sizes smaller to avoid visual conflict with the
            // other headers used to indicate which page is being downloaded
            reportLogger.beginSection(webPath + " document", heading.innerText(), headingSize + 2);
        }
    }

    private List<DownloadTask> createNestedDownloadTasks(HashMap<String, String> linkMapping, File contentRoot, int currentDepth) {
        List<DownloadTask> tasks = new LinkedList<>();
        for (Map.Entry<String, String> mappedLink : linkMapping.entrySet()) {
            String link = mappedLink.getKey();
            String path = mappedLink.getValue();
            if (path != null) {
                File localDestination = new File(contentRoot, path);
                tasks.add(new DownloadTask(link, localDestination, currentDepth + 1));
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
        if ((!contentRoot.exists() && !contentRoot.mkdirs()) || !contentRoot.isDirectory()) {
            throw new FileNotCreatedException("contentRoot does not exist and could not be created");
        }
    }
}
