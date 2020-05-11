package de.qaware.tools.sqbb.cli;

import ch.qos.logback.classic.Level;
import de.qaware.tools.sqbb.library.api.BranchMode;
import de.qaware.tools.sqbb.library.api.BreakBuildException;
import de.qaware.tools.sqbb.library.api.ProjectKey;
import de.qaware.tools.sqbb.library.api.connector.Authentication;
import de.qaware.tools.sqbb.library.impl.BranchModeParser;
import de.qaware.tools.sqbb.library.impl.BuildBreakerFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
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

    private static final Options OPTIONS = new Options();

    static {
        OPTIONS.addOption(new Option("b", "branch", true, "Sets the branch"));
        OPTIONS.addOption(new Option("bm", "branch-mode", true, "Sets the branch mode. Supported modes: projectKey, sonarQube. Default: projectKey"));
        OPTIONS.addOption(new Option("d", "debug", false, "Enables debug mode"));
    }

    public static void main(String[] args) {
        CommandLine commandLine;
        try {
            commandLine = new DefaultParser().parse(OPTIONS, args);
        } catch (ParseException e) {
            handleParseException(e);
            System.exit(EXIT_CODE_FAILURE);
            return;
        }

        if (commandLine.hasOption("debug")) {
            enableDebugMode();
        }

        LOGGER.debug("Started");
        boolean ok = false;
        try {
            new Cli().run(commandLine);
            ok = true;
        } catch (ParseException e) {
            handleParseException(e);
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

    private static void handleParseException(ParseException exception) {
        LOGGER.debug("Caught ParseException", exception);
        System.out.println("Invocation failed: " + exception.getMessage());
        new HelpFormatter().printHelp("java -jar cli.jar <project key>", OPTIONS);
    }

    @SuppressWarnings("squid:S00112") // throws exception is okay, we handle that above!
    private void run(CommandLine commandLine) throws Exception {
        if (commandLine.getArgList().isEmpty()) {
            throw new ParseException("Provide SonarQube project key as last argument");
        }

        String projectKey = commandLine.getArgList().get(commandLine.getArgList().size() - 1);
        String branch = commandLine.getOptionValue("branch");
        BranchMode branchMode = new BranchModeParser().parse(commandLine.getOptionValue("branch-mode", "projectKey"));

        // Collect information from environment and commandline arguments
        ProjectKey parsedProjectKey = ProjectKey.of(projectKey, branch);
        Authentication authentication = getAuthentication();
        String baseUrl = getBaseUrl();

        try (BuildBreakerFactory.CloseableBuildBreaker buildBreaker = BuildBreakerFactory.create(Duration.ofSeconds(10), baseUrl, authentication)) {
            buildBreaker.get().breakBuildIfNeeded(parsedProjectKey, branchMode);
        }
    }

    private static void enableDebugMode() {
        // Oh boi, this is bad, but there seems no other way
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.TRACE);

        LOGGER.debug("Enabled debug mode");
    }

    private Authentication getAuthentication() {
        return Authentication.fromToken(getEnvOrFail("SONAR_TOKEN"));
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
