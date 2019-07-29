package de.qaware.tools.sqbb.library.api.connector;

/**
 * Is thrown if there is no such project in SonarQube.
 * <p>
 * Maybe the project key is wrong?
 */
public class ProjectNotFoundException extends SonarQubeException {
    /**
     * Ctor.
     *
     * @param content content from SonarQube response
     * @param message message
     */
    public ProjectNotFoundException(String content, String message) {
        super(content, message);
    }
}
