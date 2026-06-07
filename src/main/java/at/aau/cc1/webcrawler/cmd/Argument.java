package at.aau.cc1.webcrawler.cmd;

import at.aau.cc1.webcrawler.cmd.exception.MissingArgumentValueException;
import at.aau.cc1.webcrawler.cmd.exception.UnexpectedArgumentValueExpection;

record Argument(String name, String value, boolean separated) {
    void assertHasValue() throws MissingArgumentValueException {
        if (value == null || value.isEmpty()) {
            throw new MissingArgumentValueException(name);
        }
    }

    void assertNoValue() throws UnexpectedArgumentValueExpection {
        if (value != null) {
            throw new UnexpectedArgumentValueExpection(name);
        }
    }
}
