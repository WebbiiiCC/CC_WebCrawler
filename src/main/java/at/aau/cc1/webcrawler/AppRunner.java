package at.aau.cc1.webcrawler;

import at.aau.cc1.webcrawler.cmd.ArgumentParser;
import at.aau.cc1.webcrawler.cmd.CommandConfig;
import at.aau.cc1.webcrawler.cmd.exception.MissingFinalParameterException;
import at.aau.cc1.webcrawler.cmd.exception.ParseArgumentException;
import at.aau.cc1.webcrawler.crawl.WebCrawler;
import at.aau.cc1.webcrawler.fetch.DirectDocumentFetcher;
import at.aau.cc1.webcrawler.fetch.DocumentFetcher;
import at.aau.cc1.webcrawler.fetch.HeadOnlyDocumentFetcher;
import at.aau.cc1.webcrawler.mapping.LinkMapper;
import at.aau.cc1.webcrawler.mapping.LocalLinkMapper;
import at.aau.cc1.webcrawler.mapping.translate.LinkTranslator;
import at.aau.cc1.webcrawler.mapping.translate.LocalLinkTranslator;
import at.aau.cc1.webcrawler.report.*;
import at.aau.cc1.webcrawler.storage.DiscardingStorageTarget;
import at.aau.cc1.webcrawler.storage.LocalStorageTarget;
import at.aau.cc1.webcrawler.storage.StorageTarget;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppRunner {
    public static void main(String[] arguments) {
        CommandConfig commandConfig = null;
        try {
            commandConfig = ArgumentParser.parseArguments(arguments);
        } catch (ParseArgumentException e) {
            System.err.println("Failed to parse argument: " + e.getArgument());
            System.err.println("Reason: " + e.getRawMessage());
            System.exit(1);
        } catch (MissingFinalParameterException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }

        if (commandConfig.isHelpFlag()) {
            printHelp();
            System.exit(0);
        }

        URL url = null;
        try {
            url = getCrawledUrl(commandConfig);
        } catch (Exception e) {
            System.err.println("URL can not be parsed: " + e.getMessage());
            System.exit(3);
        }

        File outputDirectory = new File(commandConfig.getOutputDirectory());
        File contentRoot = new File(outputDirectory, url.getProtocol() + "_" + url.getAuthority());

        WebCrawler crawler = makeWebCrawler(commandConfig, outputDirectory, url);
        try {
            crawler.downloadPage(url.getPath(), contentRoot, commandConfig.getMaxCrawlDepth()).join();
        } catch (IOException e) {
            System.err.println("Failed to crawl website: " + e.getMessage());
            System.exit(4);
        }
    }

    private static void printHelp() {
        System.out.println("Usage: WebCrawler [OPTION]... [URL]");
        System.out.println();
        System.out.println("Mandatory arguments to long options are mandatory for short options too.");
        System.out.println("  -d, --depth=DEPTH                 only recurse to download linked resources at most DEPTH times");
        System.out.println("                                      [Default: " + CommandConfig.DEFAULT_MAX_CRAWL_DEPTH + "]");
        System.out.println("  -h, --headOnly                    only store and resolve the website <head>");
        System.out.println("  -o, --output=DIRECTORY            store website files in this directory");
        System.out.println("                                      [Default: " + CommandConfig.DEFAULT_OUTPUT_DIRECTORY + "]");
        System.out.println("  -t, --threads=COUNT               crawl on COUNT threads");
        System.out.println("                                      [Default: " + CommandConfig.DEFAULT_THREAD_POOL_SIZE + "]");
        System.out.println("  -r, --report                      create a report file (report.md) in the website's directory");
        System.out.println("  -l, --logging                     include a protocol log in the report file");
        System.out.println("  -s, --storeHtml                   store all links as local html files");
        System.out.println("  -p, --print                       print report output to stdout");
        System.out.println("  --help                            display this help and exit");
    }

    private static URL getCrawledUrl(CommandConfig commandConfig) throws Exception {
        return URI.create(commandConfig.getCrawledUrl()).toURL();
    }

    private static WebCrawler makeWebCrawler(CommandConfig commandConfig, File outputDirectory, URL url) {
        String baseUrl = url.getProtocol() + "://" + url.getAuthority();

        ExecutorService executor = makeExecutorService(commandConfig);
        DocumentFetcher documentFetcher = makeDocumentFetcher(commandConfig);
        ReportLogger reportLogger = makeReportLogger(commandConfig, outputDirectory);
        LinkTranslator linkTranslator = new LocalLinkTranslator(baseUrl);
        LinkMapper linkMapper = new LocalLinkMapper(linkTranslator);
        StorageTarget storageTarget = makeStorageTarget(commandConfig);

        return new WebCrawler(executor, documentFetcher, reportLogger, linkMapper, storageTarget, baseUrl);
    }

    private static ExecutorService makeExecutorService(CommandConfig commandConfig) {
        int threadPoolSize = commandConfig.getThreadPoolSize();
        if (threadPoolSize == 1) {
            return Executors.newSingleThreadExecutor();
        } else {
            return Executors.newFixedThreadPool(threadPoolSize);
        }
    }

    private static DocumentFetcher makeDocumentFetcher(CommandConfig commandConfig) {
        DocumentFetcher baseDocumentFetcher = new DirectDocumentFetcher();
        if (commandConfig.isStoreHeadOnly()) {
            return new HeadOnlyDocumentFetcher(baseDocumentFetcher);
        } else {
            return baseDocumentFetcher;
        }
    }

    private static ReportLogger makeReportLogger(CommandConfig commandConfig, File outputDirectory) {
        Optional<ReportLogger> fileReportLogger = makeFileReportLogger(commandConfig, outputDirectory);
        Optional<ReportLogger> printingReportLogger = makePrintingReportLogger(commandConfig);
        if (fileReportLogger.isPresent() && printingReportLogger.isPresent()) {
            return new JointReportLogger(fileReportLogger.get(), printingReportLogger.get());
        }
        return fileReportLogger.orElseGet(() -> printingReportLogger.orElseGet(DiscardingReportLogger::new));
    }

    private static Optional<ReportLogger> makeFileReportLogger(CommandConfig commandConfig, File outputDirectory) {
        if (commandConfig.isCreateReport()) {
            File reportFile = new File(outputDirectory, "report.md");
            if (commandConfig.isLoggingReport()) {
                return Optional.of(new FileMarkdownReportLogger(reportFile));
            } else {
                return Optional.of(new NoLogFileMarkdownReportLogger(reportFile));
            }
        }
        return Optional.empty();
    }

    private static Optional<ReportLogger> makePrintingReportLogger(CommandConfig commandConfig) {
        if (commandConfig.isPrintStdout()) {
            return Optional.of(new PrintingReportLogger("Crawl", System.out));
        }
        return Optional.empty();
    }

    private static StorageTarget makeStorageTarget(CommandConfig commandConfig) {
        if (commandConfig.isStoreHtml()) {
            return new LocalStorageTarget();
        } else {
            return new DiscardingStorageTarget();
        }
    }
}
