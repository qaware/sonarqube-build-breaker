package de.qaware.tools.sqbb.cli.commandline;

import de.qaware.tools.sqbb.library.api.BranchMode;
import de.qaware.tools.sqbb.library.api.ProjectKey;

import java.util.StringJoiner;

public class CommandLineOptions {
    private final boolean debug;
    private final BranchMode branchMode;
    private final ProjectKey projectKey;

    public CommandLineOptions(boolean debug, BranchMode branchMode, ProjectKey projectKey) {
        this.debug = debug;
        this.branchMode = branchMode;
        this.projectKey = projectKey;
    }

    public boolean isDebug() {
        return debug;
    }

    public BranchMode getBranchMode() {
        return branchMode;
    }

    public ProjectKey getProjectKey() {
        return projectKey;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CommandLineOptions.class.getSimpleName() + "[", "]")
            .add("debug=" + debug)
            .add("branchMode=" + branchMode)
            .add("projectKey=" + projectKey)
            .toString();
    }
}
