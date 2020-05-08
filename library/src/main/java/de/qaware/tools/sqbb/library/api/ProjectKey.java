package de.qaware.tools.sqbb.library.api;

import javax.annotation.Nullable;
import java.util.StringJoiner;

/**
 * Identifies a project in SonarQube.
 */
public class ProjectKey {
    /**
     * SonarQube project key. Can be found on the HTML page of the SonarQube project.
     */
    private final String key;
    @Nullable
    private final String branch;

    private ProjectKey(String key, @Nullable String branch) {
        this.key = key;
        this.branch = branch;
    }

    public String getKey() {
        return key;
    }

    @Nullable
    public String getBranch() {
        return branch;
    }

    /**
     * Creates a project key of the given String.
     *
     * @param key string
     * @return project key
     */
    public static ProjectKey of(String key, @Nullable String branch) {
        return new ProjectKey(key, branch);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ProjectKey.class.getSimpleName() + "[", "]")
            .add("key='" + key + "'")
            .add("branch='" + branch + "'")
            .toString();
    }
}
