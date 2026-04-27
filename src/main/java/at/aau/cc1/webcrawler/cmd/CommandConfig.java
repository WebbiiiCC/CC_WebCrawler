package at.aau.cc1.webcrawler.cmd;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(AccessLevel.PACKAGE)
@ToString
public class CommandConfig {
    public static final int DEFAULT_MAX_CRAWL_DEPTH = 10;
    public static final String DEFAULT_OUTPUT_DIRECTORY = "./crawl";

    private String crawledUrl;
    private int maxCrawlDepth = DEFAULT_MAX_CRAWL_DEPTH;
    private boolean storeHeadOnly = false;
    private boolean createReport = false;
    private String outputDirectory = DEFAULT_OUTPUT_DIRECTORY;
}
