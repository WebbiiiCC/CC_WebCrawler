package at.aau.cc1.webcrawler.cmd;

public class ArgumentParser {
    public static CommandConfig parseArguments(String[] arguments) {
        CommandConfig commandConfig = new CommandConfig();
        int parserIndex = 0;
        while (parserIndex < arguments.length) {
            String argument = arguments[parserIndex];

            if (!argument.startsWith("-")) break;

            Argument parsedArgument = parseArgument(argument, arguments, parserIndex);
            if (parsedArgument == null || (parsedArgument.value() != null && parsedArgument.value().isEmpty())) {
                System.err.println("Invalid command argument: " + argument);
                return null;
            }
            try {
                switch (parsedArgument.name()) {
                    case "--depth", "-d" -> {
                        parsedArgument.assertHasValue();
                        commandConfig.setMaxCrawlDepth(Integer.parseUnsignedInt(parsedArgument.value()));
                    }
                    case "--headOnly", "-h" -> {
                        parsedArgument.assertNoValue();
                        commandConfig.setStoreHeadOnly(true);
                    }
                    case "--report", "-r" -> {
                        parsedArgument.assertNoValue();
                        commandConfig.setCreateReport(true);
                    }
                    case "--output", "-o" -> {
                        parsedArgument.assertHasValue();
                        commandConfig.setOutputDirectory(parsedArgument.value());
                    }
                    case "--help" -> {
                        parsedArgument.assertNoValue();
                        commandConfig.setHelpFlag(true);
                    }
                    default -> {
                        System.err.println("Unknown argument: " + parsedArgument.name());
                    }
                }
            } catch (NullPointerException e) {
                System.err.println("Argument expected a value: " + parsedArgument.name());
                return null;
            } catch (NumberFormatException e) {
                System.err.println("Argument expected a number value: " + parsedArgument.name());
                return null;
            } catch (Exception ex) {
                System.err.println("Failed to parse argument: " + parsedArgument.name());
                ex.printStackTrace();
                return null;
            }

            if (parsedArgument.separated()) {
                parserIndex += 2;
            } else {
                parserIndex += 1;
            }
        }
        if (parserIndex == arguments.length) {
            System.err.println("No URL to crawl provided!");
            return null;
        }
        commandConfig.setCrawledUrl(arguments[parserIndex]);
        return commandConfig;
    }

    private static Argument parseArgument(String argument, String[] arguments, int index) {
        if (argument.startsWith("--") && argument.contains("=")) {
            String[] keyValuePair = argument.split("=");
            if (keyValuePair.length != 2) {
                return null;
            }
            String key = keyValuePair[0];
            String value = keyValuePair[1];
            return new Argument(key, value, false);
        } else if (index + 2 < arguments.length) {
            String value = arguments[index + 1];
            if (!value.startsWith("-")) {
                return new Argument(argument, value, true);
            }
        }
        return new Argument(argument, null, false);
    }
}
