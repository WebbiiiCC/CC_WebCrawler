package at.aau.cc1.webcrawler.cmd;

import at.aau.cc1.webcrawler.cmd.exception.ParseArgumentException;
import at.aau.cc1.webcrawler.cmd.exception.ParseNumberArgumentException;
import at.aau.cc1.webcrawler.cmd.exception.UnknownArgumentException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(AccessLevel.PACKAGE)
@ToString
public class CommandConfig {
    public static final int DEFAULT_MAX_CRAWL_DEPTH = 10;
    public static final int DEFAULT_THREAD_POOL_SIZE = 1;
    public static final String DEFAULT_OUTPUT_DIRECTORY = "./crawl";

    private String crawledUrl;
    private int maxCrawlDepth = DEFAULT_MAX_CRAWL_DEPTH;
    private boolean storeHeadOnly = false;
    private boolean createReport = false;
    private boolean loggingReport = false;
    private boolean storeHtml = false;
    private boolean printStdout = false;
    private String outputDirectory = DEFAULT_OUTPUT_DIRECTORY;
    private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
    private boolean helpFlag = false;

    void setArgumentByName(Argument argument) throws ParseArgumentException {
        switch (argument.name()) {
            case "--depth", "-d" -> {
                argument.assertHasValue();

                int depth;
                try {
                    depth = Integer.parseUnsignedInt(argument.value());
                } catch (NumberFormatException e) {
                    throw new ParseNumberArgumentException(argument.name());
                }
                this.setMaxCrawlDepth(depth);
            }
            case "--headOnly", "-h" -> {
                argument.assertNoValue();
                this.setStoreHeadOnly(true);
            }
            case "--report", "-r" -> {
                argument.assertNoValue();
                this.setCreateReport(true);
            }
            case "--logging", "-l" -> {
                argument.assertNoValue();
                this.setLoggingReport(true);
            }
            case "--storeHtml", "-s" -> {
                argument.assertNoValue();
                this.setStoreHtml(true);
            }
            case "--output", "-o" -> {
                argument.assertHasValue();
                this.setOutputDirectory(argument.value());
            }
            case "--threads", "-t" -> {
                argument.assertHasValue();

                int threads;
                try {
                    threads = Integer.parseUnsignedInt(argument.value());
                } catch (NumberFormatException e) {
                    throw new ParseNumberArgumentException(argument.name());
                }
                this.setThreadPoolSize(threads);
            }
            case "--print", "-p" -> {
                argument.assertNoValue();
                this.setPrintStdout(true);
            }
            case "--help" -> {
                argument.assertNoValue();
                this.setHelpFlag(true);
            }
            default -> throw new UnknownArgumentException(argument.name());
        }
    }
}
