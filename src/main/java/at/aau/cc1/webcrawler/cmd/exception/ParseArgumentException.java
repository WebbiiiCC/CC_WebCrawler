package at.aau.cc1.webcrawler.cmd.exception;

import lombok.Getter;

public class ParseArgumentException extends Exception {
    @Getter
    private final String rawMessage;
    @Getter
    private final String argument;

    public ParseArgumentException(String message, String argument) {
        super(message + ": " + argument);
        this.rawMessage = message;
        this.argument = argument;
    }
}
