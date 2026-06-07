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
import at.aau.cc1.webcrawler.report.DiscardingReportLogger;
import at.aau.cc1.webcrawler.report.FileMarkdownReportLogger;
import at.aau.cc1.webcrawler.report.ReportLogger;
import at.aau.cc1.webcrawler.storage.LocalStorageTarget;
import at.aau.cc1.webcrawler.storage.StorageTarget;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

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
            crawler.downloadPage(url.getPath(), contentRoot, commandConfig.getMaxCrawlDepth());
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
        System.out.println("  -r, --report                      create a report file (report.md) in the website's directory");
        System.out.println("  --help                            display this help and exit");
    }

    private static URL getCrawledUrl(CommandConfig commandConfig) throws Exception {
        return URI.create(commandConfig.getCrawledUrl()).toURL();
    }

    private static WebCrawler makeWebCrawler(CommandConfig commandConfig, File outputDirectory, URL url) {
        String baseUrl = url.getProtocol() + "://" + url.getAuthority();

        DocumentFetcher documentFetcher = makeDocumentFetcher(commandConfig);
        ReportLogger reportLogger = makeReportLogger(commandConfig, outputDirectory);
        LinkTranslator linkTranslator = new LocalLinkTranslator(baseUrl);
        LinkMapper linkMapper = new LocalLinkMapper(linkTranslator);
        StorageTarget storageTarget = new LocalStorageTarget();

        return new WebCrawler(documentFetcher, reportLogger, linkMapper, storageTarget, baseUrl);
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
        if (commandConfig.isCreateReport()) {
            File reportFile = new File(outputDirectory, "report.md");
            return new FileMarkdownReportLogger(reportFile);
        } else {
            return new DiscardingReportLogger();
        }
    }
}
