package at.aau.cc1.webcrawler.cmd.exception;

public class MissingArgumentValueException extends ParseArgumentException {
    public MissingArgumentValueException(String argument) {
        super("Missing value for argument", argument);
    }
}
