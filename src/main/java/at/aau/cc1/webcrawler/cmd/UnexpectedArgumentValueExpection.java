package at.aau.cc1.webcrawler.cmd;

public class UnexpectedArgumentValueExpection extends RuntimeException {
    public UnexpectedArgumentValueExpection(String argument) {
        super("Argument does not take a value: " + argument);
    }
}
