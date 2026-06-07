package at.aau.cc1.webcrawler.cmd.exception;

public class UnknownArgumentException extends ParseArgumentException {
    public UnknownArgumentException(String argument) {
        super("Unknown argument", argument);
    }
}
