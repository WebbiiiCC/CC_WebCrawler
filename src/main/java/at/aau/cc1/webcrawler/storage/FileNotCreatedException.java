package at.aau.cc1.webcrawler.storage;

public class FileNotCreatedException extends RuntimeException {
    public FileNotCreatedException(String message) {
        super(message);
    }
}
