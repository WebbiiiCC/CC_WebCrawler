package at.aau.cc1.webcrawler.cmd.exception;

public class ParseNumberArgumentException extends ParseArgumentException {
    public ParseNumberArgumentException(String argument) {
        super("Argument expects a number value", argument);
    }
}
