package de.qaware.tools.sqbb.library.impl;

import de.qaware.tools.sqbb.library.api.BranchMode;

/**
 * Parses an input string to a {@link BranchMode}.
 */
public class BranchModeParser {
    /**
     * Parses the given input string to a {@link BranchMode}.
     *
     * @param input input string to parse
     * @return parsed branch mode
     * @throws IllegalArgumentException if the given string can't be parsed
     */
    public BranchMode parse(String input) {
        switch (input) {
            case "projectKey":
                return BranchMode.PROJECT_KEY;
            case "sonarQube":
                return BranchMode.SONARQUBE;
            default:
                throw new IllegalArgumentException(String.format("Failed to parse branch mode. Supported values: [projectKey, sonarQube]. Was: '%s'", input));
        }
    }
}
