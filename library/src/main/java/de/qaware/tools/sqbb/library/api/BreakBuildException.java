package de.qaware.tools.sqbb.library.api;

/**
 * Is thrown if the build needs to be broken.
 */
public class BreakBuildException extends Exception {
    /**
     * Ctor.
     *
     * @param message message
     */
    public BreakBuildException(String message) {
        super(message);
    }
}
