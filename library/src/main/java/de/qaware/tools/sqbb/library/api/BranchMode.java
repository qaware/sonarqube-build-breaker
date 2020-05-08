package de.qaware.tools.sqbb.library.api;

/**
 * Branch mode.
 */
public enum BranchMode {
    /**
     * Branch is appended to the project key.
     */
    PROJECT_KEY,
    /**
     * Use native branch support from SonarQube.
     */
    SONARQUBE
}
