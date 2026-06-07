package at.aau.cc1.webcrawler.cmd;

import at.aau.cc1.webcrawler.cmd.exception.MissingArgumentValueException;
import at.aau.cc1.webcrawler.cmd.exception.MissingFinalParameterException;
import at.aau.cc1.webcrawler.cmd.exception.ParseArgumentException;

public class ArgumentParser {
    public static CommandConfig parseArguments(String[] arguments) throws ParseArgumentException, MissingFinalParameterException {
        CommandConfig commandConfig = new CommandConfig();
        int parserIndex = 0;
        while (parserIndex < arguments.length) {
            String argument = arguments[parserIndex];
            if (!argument.startsWith("-")) break;

            Argument parsedArgument = parseArgument(argument, arguments, parserIndex);
            commandConfig.setArgumentByName(parsedArgument);

            if (parsedArgument.separated()) {
                parserIndex += 2;
            } else {
                parserIndex += 1;
            }
        }
        if (parserIndex == arguments.length) {
            throw new MissingFinalParameterException("No URL to crawl provided!");
        }
        commandConfig.setCrawledUrl(arguments[parserIndex]);
        return commandConfig;
    }

    private static Argument parseArgument(String argument, String[] arguments, int index) throws MissingArgumentValueException {
        if (argument.startsWith("--") && argument.contains("=")) {
            String[] keyValuePair = argument.split("=");
            if (keyValuePair.length != 2) {
                throw new MissingArgumentValueException(argument);
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
