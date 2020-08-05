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

    @Parameter(property = "sqbb.projectKey", defaultValue = "${project.groupId}:${project.artifactId}")
    private String projectKey;
    @Parameter(property = "sqbb.branch")
    @Nullable
    private String branch;
    @Parameter(property = "sqbb.sonarQubeUrl", required = true)
    private String sonarQubeUrl;
    @Parameter(property = "sqbb.sonarQubeToken", required = true)
    private String sonarQubeToken;
    @Parameter(property = "sqbb.waitTime", defaultValue = "10")
    private long waitTime;
    @Parameter(property = "sqbb.branchMode", defaultValue = "projectKey")
    private String branchMode;
    @Parameter(property = "sqbb.skip", defaultValue = "false")
    private boolean skip;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            LOGGER.info("Skipping SonarQube build breaker execution");
            return;
        }

        BranchMode branchMode = new BranchModeParser().parse(this.branchMode);
        if (branch == null) {
            LOGGER.info("Running SonarQube build breaker on project {}", projectKey);
        } else {
            LOGGER.info("Running SonarQube build breaker on project {}, branch {} (mode: {})", projectKey, branch, branchMode);
        }

        try (BuildBreakerFactory.CloseableBuildBreaker buildBreaker = BuildBreakerFactory.create(Duration.ofSeconds(waitTime), sonarQubeUrl, Authentication.fromToken(sonarQubeToken))) {
            buildBreaker.get().breakBuildIfNeeded(ProjectKey.of(projectKey, branch), branchMode);
        } catch (BreakBuildException e) {
            throw new MojoFailureException("SonarQube build breaker", e);
        } catch (Exception e) {
            throw new MojoExecutionException("Exception while running build breaker", e);
        }
    }
}
