package at.aau.cc1.webcrawler.cmd;

import at.aau.cc1.webcrawler.cmd.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ArgumentParserTest {
    private final String url = "https://example.org/";

    @Test
    public void testOnlyUrl() {
        CommandConfig commandConfig = assertDoesNotThrow(() -> ArgumentParser.parseArguments(new String[]{url}));
        assertCorrectCommandConfig(commandConfig, CommandConfig.DEFAULT_MAX_CRAWL_DEPTH, false, false, CommandConfig.DEFAULT_OUTPUT_DIRECTORY);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-d 4", "--depth 4", "--depth=4"})
    public void testDepth(String argument) {
        CommandConfig commandConfig = assertDoesNotThrow(() -> ArgumentParser.parseArguments((argument + " " + url).split(" ")));
        assertCorrectCommandConfig(commandConfig, 4, false, false, CommandConfig.DEFAULT_OUTPUT_DIRECTORY);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-o /tmp/dir", "--output /tmp/dir", "--output=/tmp/dir"})
    public void testOutputDirectory(String argument) {
        CommandConfig commandConfig = assertDoesNotThrow(() -> ArgumentParser.parseArguments((argument + " " + url).split(" ")));
        assertCorrectCommandConfig(commandConfig, CommandConfig.DEFAULT_MAX_CRAWL_DEPTH, false, false, "/tmp/dir");
    }

    @ParameterizedTest
    @ValueSource(strings = {"-h", "--headOnly"})
    public void testHeadOnly(String argument) {
        CommandConfig commandConfig = assertDoesNotThrow(() -> ArgumentParser.parseArguments(new String[]{argument, url}));
        assertCorrectCommandConfig(commandConfig, CommandConfig.DEFAULT_MAX_CRAWL_DEPTH, true, false, CommandConfig.DEFAULT_OUTPUT_DIRECTORY);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-r", "--report"})
    public void testCreateReport(String argument) {
        CommandConfig commandConfig = assertDoesNotThrow(() -> ArgumentParser.parseArguments(new String[]{argument, url}));
        assertCorrectCommandConfig(commandConfig, CommandConfig.DEFAULT_MAX_CRAWL_DEPTH, false, true, CommandConfig.DEFAULT_OUTPUT_DIRECTORY);
    }

    @ParameterizedTest
    @ValueSource(strings = {"--depth=", "--output=", "-o", "-d", "--depth", "--output"})
    public void testArgumentsWithMissingValues(String argumentsInput) {
        assertThrows(MissingArgumentValueException.class, () -> ArgumentParser.parseArguments((argumentsInput + " " + url).split(" ")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"--storeHtml test", "-s test", "-r abc", "-l asdf"})
    public void testArgumentsWithUnexpectedValue(String argumentsInput) {
        assertThrows(UnexpectedArgumentValueException.class, () -> ArgumentParser.parseArguments((argumentsInput + " " + url).split(" ")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"--depth=test", "--depth=true", "-d abc"})
    public void testNonNumericDepth(String argumentsInput) {
        assertThrows(ParseNumberArgumentException.class, () -> ArgumentParser.parseArguments((argumentsInput + " " + url).split(" ")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"--unknownArgument", "--whatIsThis", "-n o"})
    public void testUnknownArgument(String argumentsInput) {
        assertThrows(UnknownArgumentException.class, () -> ArgumentParser.parseArguments((argumentsInput + " " + url).split(" ")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-r", "--report", "--depth=4", "-h"})
    public void testMissingUrl(String argument) {
        assertThrows(MissingFinalParameterException.class, () -> ArgumentParser.parseArguments(argument.split(" ")));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "-d 4 -h -r;4;true;true;",
            "-h -o /tmp/dir;;true;false;/tmp/dir",
            "--headOnly -r;;true;true;",
            "-o /tmp/dir -r -l --depth=2;2;false;true;/tmp/dir",
            "-h --depth 3 --report;3;true;true;"
    }, delimiter = ';')
    public void testCombinedArguments(String argumentsInput, Integer depth, boolean headOnly, boolean createReport, String outputDirectory) {
        if (depth == null) {
            depth = CommandConfig.DEFAULT_MAX_CRAWL_DEPTH;
        }
        if (outputDirectory == null) {
            outputDirectory = CommandConfig.DEFAULT_OUTPUT_DIRECTORY;
        }

        CommandConfig commandConfig = assertDoesNotThrow(() -> ArgumentParser.parseArguments((argumentsInput + " " + url).split(" ")));
        assertCorrectCommandConfig(commandConfig, depth, headOnly, createReport, outputDirectory);
    }

    private void assertCorrectCommandConfig(CommandConfig commandConfig, int depth, boolean headOnly, boolean createReport, String outputDirectory) {
        assertNotNull(commandConfig);
        assertEquals(url, commandConfig.getCrawledUrl());
        assertEquals(depth, commandConfig.getMaxCrawlDepth());
        assertEquals(headOnly, commandConfig.isStoreHeadOnly());
        assertEquals(createReport, commandConfig.isCreateReport());
        assertEquals(outputDirectory, commandConfig.getOutputDirectory());
    }
}
