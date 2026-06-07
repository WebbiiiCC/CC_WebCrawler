package at.aau.cc1.webcrawler.cmd.exception;

public class UnexpectedArgumentValueExpection extends ParseArgumentException {
    public UnexpectedArgumentValueExpection(String argument) {
        super("Argument does not take a value", argument);
    }
}
