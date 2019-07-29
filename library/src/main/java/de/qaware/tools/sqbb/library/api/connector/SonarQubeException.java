package de.qaware.tools.sqbb.library.api.connector;

/**
 * General SonarQube exception which is thrown if we don't know the exact cause.
 */
public class SonarQubeException extends Exception {
    private final String content;

    /**
     * Ctor.
     *
     * @param content content from SonarQube response
     * @param message message
     */
    public SonarQubeException(String content, String message) {
        super(message);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
