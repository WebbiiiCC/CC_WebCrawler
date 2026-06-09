package at.aau.cc1.webcrawler.cmd.exception;

public class UnexpectedArgumentValueException extends ParseArgumentException {
    public UnexpectedArgumentValueException(String argument) {
        super("Argument does not take a value", argument);
    }
}
