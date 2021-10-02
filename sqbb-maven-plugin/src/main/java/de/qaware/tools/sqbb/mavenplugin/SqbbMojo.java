package de.qaware.tools.sqbb.mavenplugin;

import de.qaware.tools.sqbb.library.api.BranchMode;
import de.qaware.tools.sqbb.library.api.BreakBuildException;
import de.qaware.tools.sqbb.library.api.ProjectKey;
import de.qaware.tools.sqbb.library.api.connector.Authentication;
import de.qaware.tools.sqbb.library.impl.BranchModeParser;
import de.qaware.tools.sqbb.library.impl.BuildBreakerFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.time.Duration;

/**
 * Maven Mojo which runs the SonarQube build breaker.
 */
@Mojo(name = "sqbb")
public class SqbbMojo extends AbstractMojo {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqbbMojo.class);

    // See https://docs.sonarqube.org/latest/analysis/analysis-parameters/
    private static final String DEFAULT_SONARQUBE_URL = "http://localhost:9000";

    @Parameter(property = "sqbb.projectKey")
    @Nullable
    private String projectKey;
    @Parameter(property = "sqbb.branch")
    @Nullable
    private String branch;
    @Parameter(property = "sqbb.sonarQubeUrl")
    @Nullable
    private String sonarQubeUrl;
    @Parameter(property = "sqbb.sonarQubeToken")
    @Nullable
    private String sonarQubeToken;
    @Parameter(property = "sqbb.sonarQubeUsername")
    @Nullable
    private String sonarQubeUsername;
    @Parameter(property = "sqbb.sonarQubePassword")
    @Nullable
    private String sonarQubePassword;
    @Parameter(property = "sqbb.waitTime", defaultValue = "10")
    private long waitTime;
    @Parameter(property = "sqbb.branchMode", defaultValue = "projectKey")
    private String branchMode;
    @Parameter(property = "sqbb.skip", defaultValue = "false")
    private boolean skip;

    @Parameter(defaultValue = "${project.groupId}:${project.artifactId}")
    private String defaultProjectKey;

    @Parameter(property = "sonar.host.url")
    @Nullable
    private String fallbackUrl;
    @Parameter(property = "sonar.login")
    @Nullable
    private String fallbackLogin;
    @Parameter(property = "sonar.password")
    @Nullable
    private String fallbackPassword;
    @Parameter(property = "sonar.projectKey")
    @Nullable
    private String fallbackProjectKey;
    @Parameter(property = "sonar.branch.name")
    @Nullable
    private String fallbackBranch;

    public SqbbMojo() {
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            LOGGER.info("Skipping SonarQube build breaker execution");
            return;
        }

        String effectiveBranch = getBranch();
        String effectiveProjectKey = getProjectKey();

        BranchMode branchMode = new BranchModeParser().parse(this.branchMode);
        if (effectiveBranch == null) {
            LOGGER.info("Running SonarQube build breaker on project {}", effectiveProjectKey);
        } else {
            LOGGER.info("Running SonarQube build breaker on project {}, branch {} (mode: {})", effectiveProjectKey, effectiveBranch, branchMode);
        }

        try (BuildBreakerFactory.CloseableBuildBreaker buildBreaker = BuildBreakerFactory.create(Duration.ofSeconds(waitTime), getSonarQubeUrl(), getAuthentication())) {
            buildBreaker.get().breakBuildIfNeeded(ProjectKey.of(effectiveProjectKey, effectiveBranch), branchMode);
        } catch (BreakBuildException e) {
            throw new MojoFailureException("SonarQube build breaker", e);
        } catch (Exception e) {
            throw new MojoExecutionException("Exception while running build breaker", e);
        }
    }

    private String getProjectKey() {
        if (projectKey != null) {
            return projectKey;
        }

        if (fallbackProjectKey != null) {
            return fallbackProjectKey;
        }

        return defaultProjectKey;
    }

    @Nullable
    private String getBranch() {
        if (branch != null) {
            return branch;
        }

        if (fallbackBranch != null) {
            return fallbackBranch;
        }

        return null;
    }

    private String getSonarQubeUrl() {
        if (sonarQubeUrl != null) {
            return sonarQubeUrl;
        }

        if (fallbackUrl != null) {
            return fallbackUrl;
        }

        return DEFAULT_SONARQUBE_URL;
    }

    private Authentication getAuthentication() throws MojoFailureException {
        if (sonarQubeToken != null) {
            LOGGER.debug("Using authentication from sqbb.sonarQubeToken");
            return Authentication.fromToken(sonarQubeToken);
        }

        if (sonarQubeUsername != null && sonarQubePassword != null) {
            LOGGER.debug("Using authentication from sqbb.sonarQubeUsername and sqbb.sonarQubePassword");
            return Authentication.fromUsernameAndPassword(sonarQubeUsername, sonarQubePassword);
        }

        if (fallbackPassword != null && fallbackLogin != null) {
            // Password is set -> username / password auth
            // See https://docs.sonarqube.org/latest/analysis/analysis-parameters/
            LOGGER.debug("Using authentication from sonar.login and sonar.password");
            return Authentication.fromUsernameAndPassword(fallbackLogin, fallbackPassword);
        }

        if (fallbackLogin != null) {
            // Only login is set -> token auth
            // See https://docs.sonarqube.org/latest/analysis/analysis-parameters/
            LOGGER.debug("Using authentication from sonar.login");
            return Authentication.fromToken(fallbackLogin);
        }

        throw new MojoFailureException("No authentication settings (sqbb.sonarQubeToken, sqbb.sonarQubeUsername or sqbb.sonarQubePassword) have been found");
    }
}
