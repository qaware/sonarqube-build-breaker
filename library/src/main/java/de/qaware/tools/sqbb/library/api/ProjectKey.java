package de.qaware.tools.sqbb.library.api;

/**
 * Identifies a project in SonarQube.
 */
public class ProjectKey {
    /**
     * SonarQube project key. Can be found on the HTML page of the SonarQube project.
     */
    private final String key;

    private ProjectKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    /**
     * Creates a project key of the given String.
     *
     * @param key string
     * @return project key
     */
    public static ProjectKey of(String key) {
        return new ProjectKey(key);
    }

    @Override
    public String toString() {
        return key;
    }
}
