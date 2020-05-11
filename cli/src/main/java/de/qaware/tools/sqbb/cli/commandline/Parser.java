package de.qaware.tools.sqbb.cli.commandline;

import de.qaware.tools.sqbb.library.api.BranchMode;
import de.qaware.tools.sqbb.library.api.ProjectKey;
import de.qaware.tools.sqbb.library.impl.BranchModeParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class);
    private static final Options OPTIONS = new Options();

    static {
        OPTIONS.addOption(new Option("b", "branch", true, "Sets the branch"));
        OPTIONS.addOption(new Option("bm", "branch-mode", true, "Sets the branch mode. Supported modes: projectKey, sonarQube. Default: projectKey"));
        OPTIONS.addOption(new Option("d", "debug", false, "Enables debug mode"));
    }

    public CommandLineOptions parse(String... args) throws ParseException {
        CommandLine commandLine = new DefaultParser().parse(OPTIONS, args);

        if (commandLine.getArgList().isEmpty()) {
            throw new ParseException("Provide SonarQube project key as last argument");
        }

        boolean debug = commandLine.hasOption("debug");
        String branch = commandLine.getOptionValue("branch");
        BranchMode branchMode = parseBranchMode(commandLine.getOptionValue("branch-mode", "projectKey"));
        // Project key is always last argument
        String projectKey = commandLine.getArgList().get(commandLine.getArgList().size() - 1);

        return new CommandLineOptions(debug, branchMode, ProjectKey.of(projectKey, branch));
    }

    private BranchMode parseBranchMode(String branchMode) throws ParseException {
        try {
            return new BranchModeParser().parse(branchMode);
        } catch (IllegalArgumentException e) {
            LOGGER.debug("Failed to parse branch mode from '{}'", branchMode, e);
            throw new ParseException(e.getMessage());
        }
    }

    public void printHelp() {
        new HelpFormatter().printHelp("java -jar cli.jar <project key>", OPTIONS);
    }
}
