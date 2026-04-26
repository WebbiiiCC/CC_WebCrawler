package at.aau.cc1.webcrawler.crawl;

import java.io.File;

record DownloadTask(String webPath, File localDestination, int depth) {
}
