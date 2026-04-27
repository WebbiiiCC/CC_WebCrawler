package at.aau.cc1.webcrawler.cmd;

record Argument(String name, String value, boolean separated) {
    void assertHasValue() {
        if (value == null) {
            throw new NullPointerException("Argument value is null");
        }
    }

    void assertNoValue() {
        if (value != null) {
            throw new UnexpectedArgumentValueExpection(name);
        }
    }
}
