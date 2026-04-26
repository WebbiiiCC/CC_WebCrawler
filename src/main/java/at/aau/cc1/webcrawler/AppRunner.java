package at.aau.cc1.webcrawler;

import at.aau.cc1.webcrawler.crawl.WebCrawler;
import at.aau.cc1.webcrawler.fetch.DirectDocumentFetcher;
import at.aau.cc1.webcrawler.mapping.LocalLinkMapper;
import at.aau.cc1.webcrawler.mapping.translate.LocalLinkTranslator;

import java.io.File;
import java.net.URI;
import java.net.URL;

public class AppRunner {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: AppRunner <URL> <MaxDepth>");
            System.exit(1);
        }
        URL url = URI.create(args[0]).toURL();
        String baseUrl = url.getProtocol() + "://" + url.getAuthority();
        int maxDepth = Integer.parseInt(args[1]);

        File contentRoot = new File("crawl/" + url.getProtocol() + "_" + url.getHost() + "_" + url.getPort(), url.getPath());

        WebCrawler crawler = new WebCrawler(new DirectDocumentFetcher(), new LocalLinkMapper(new LocalLinkTranslator(baseUrl)), baseUrl);
        crawler.downloadPage("/", contentRoot, maxDepth);
    }
}
