package de.qaware.tools.sqbb.cli;

import ch.qos.logback.classic.Level;
import de.qaware.tools.sqbb.cli.commandline.CommandLineOptions;
import de.qaware.tools.sqbb.cli.commandline.Parser;
import de.qaware.tools.sqbb.library.api.BreakBuildException;
import de.qaware.tools.sqbb.library.api.connector.Authentication;
import de.qaware.tools.sqbb.library.impl.BuildBreakerFactory;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * CLI program which can be executed in a CI pipeline.
 * <p>
 * It waits until a pending analysis is done. It
 * then breaks the build if:
 * <p>
 * - the analysis failed (can happen if the database is corrupt, SonarQube issues, etc.)
 * - if the quality gate of the project is in error state (red)
 * <p>
 * A broken build is signalled with the exit code `1`. If everything is okay, the exit code is `0`.
 */
public class Cli {
    private static final Logger LOGGER = LoggerFactory.getLogger(Cli.class);
    private static final int EXIT_CODE_SUCCESS = 0;
    private static final int EXIT_CODE_FAILURE = 1;

    public static void main(String[] args) {
        Parser parser = new Parser();
        CommandLineOptions commandLine;
        try {
            commandLine = parser.parse(args);
        } catch (ParseException e) {
            LOGGER.debug("Caught ParseException", e);
            LOGGER.error("Invocation failed: {}", e.getMessage());
            parser.printHelp();
            System.exit(EXIT_CODE_FAILURE);
            return;
        }

        if (commandLine.isDebug()) {
            enableDebugMode();
        }

        LOGGER.debug("Started with command line {}", commandLine);
        boolean ok = false;
        try {
            new Cli().run(commandLine);
            ok = true;
        } catch (BreakBuildException e) {
            LOGGER.error("Build broken! Reason: {}", e.getMessage());
            LOGGER.debug("Exception details", e);
        } catch (Exception e) {
            LOGGER.error("Boom goes the program", e);
        } finally {
            LOGGER.debug("Stopped");

            System.exit(ok ? EXIT_CODE_SUCCESS : EXIT_CODE_FAILURE);
        }
    }

    @SuppressWarnings("squid:S00112") // throws exception is okay, we handle that above!
    private void run(CommandLineOptions commandLine) throws Exception {
        // Collect information from environment and commandline arguments
        Authentication authentication = getAuthentication();
        String baseUrl = getBaseUrl();

        try (BuildBreakerFactory.CloseableBuildBreaker buildBreaker = BuildBreakerFactory.create(Duration.ofSeconds(10), baseUrl, authentication)) {
            buildBreaker.get().breakBuildIfNeeded(commandLine.getProjectKey(), commandLine.getBranchMode());
        }
    }

    private static void enableDebugMode() {
        // Oh boi, this is bad, but there seems no other way
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.TRACE);

        LOGGER.debug("Enabled debug mode");
    }

    private Authentication getAuthentication() {
        String username = System.getenv("SONAR_USERNAME");
        String password = System.getenv("SONAR_PASSWORD");
        String token = System.getenv("SONAR_TOKEN");

        if (token != null) {
            LOGGER.debug("Using authentication from SONAR_TOKEN env variable");
            return Authentication.fromToken(token);
        }

        if (username != null && password != null) {
            LOGGER.debug("Using authentication from SONAR_USERNAME and SONAR_PASSWORD env variables");
            return Authentication.fromUsernameAndPassword(username, password);
        }

        throw new IllegalStateException("No authentication variables (SONAR_USERNAME, SONAR_PASSWORD or SONAR_TOKEN) have been found");
    }

    private String getBaseUrl() {
        return getEnvOrFail("SONAR_URL");
    }

    private String getEnvOrFail(String variable) {
        String result = System.getenv(variable);
        if (result == null) {
            LOGGER.error("{} environment variable is not set", variable);
            throw new IllegalStateException(variable + " environment variable is not set");
        }
        return result;
    }
}
