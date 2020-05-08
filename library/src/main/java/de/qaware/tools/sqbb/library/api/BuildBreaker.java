package de.qaware.tools.sqbb.library.api;

import de.qaware.tools.sqbb.library.api.connector.SonarQubeException;

import java.io.IOException;

/**
 * Breaks the build if needed.
 */
public interface BuildBreaker {
    /**
     * Breaks the build if needed.
     * <p>
     * Waits until a pending analysis is done. Then breaks the build if:
     * <p>
     * - the analysis failed (can happen if the database is corrupt, SonarQube issues, etc.)
     * - if the quality gate of the project is in error state (red)
     *
     * @param projectKey project
     * @param branchMode branch mode
     * @throws IOException          if a I/O failure occured while connecting to SonarQube
     * @throws SonarQubeException   if SonarQube returned an error
     * @throws InterruptedException if the thread got interrupted while waiting for the next analysis queue polling
     * @throws BreakBuildException  if the build needs to be broken
     */
    void breakBuildIfNeeded(ProjectKey projectKey, BranchMode branchMode) throws IOException, SonarQubeException, InterruptedException, BreakBuildException;
}
