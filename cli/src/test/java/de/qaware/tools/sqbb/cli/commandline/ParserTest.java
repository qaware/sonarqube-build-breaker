package de.qaware.tools.sqbb.cli.commandline;

import de.qaware.tools.sqbb.library.api.BranchMode;
import de.qaware.tools.sqbb.library.api.ProjectKey;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ParserTest {
    private Parser sut;

    @BeforeEach
    void setUp() {
        sut = new Parser();
    }

    @Test
    void defaults() throws ParseException {
        CommandLineOptions options = sut.parse("project-1");

        assertThat(options.getProjectKey()).isEqualTo(ProjectKey.of("project-1", null));
        assertThat(options.getBranchMode()).isEqualTo(BranchMode.PROJECT_KEY);
        assertThat(options.isDebug()).isFalse();
    }

    @Test
    void branch() throws ParseException {
        CommandLineOptions options = sut.parse("--branch", "master", "project-1");

        assertThat(options.getProjectKey()).isEqualTo(ProjectKey.of("project-1", "master"));
    }

    @Test
    void branchMode_projectKey() throws ParseException {
        CommandLineOptions options = sut.parse("--branch-mode", "projectKey", "project-1");

        assertThat(options.getBranchMode()).isEqualTo(BranchMode.PROJECT_KEY);
    }

    @Test
    void branchMode_sonarQube() throws ParseException {
        CommandLineOptions options = sut.parse("--branch-mode", "sonarQube", "project-1");

        assertThat(options.getBranchMode()).isEqualTo(BranchMode.SONARQUBE);
    }

    @Test
    void debug() throws ParseException {
        CommandLineOptions options = sut.parse("--debug", "project-1");

        assertThat(options.isDebug()).isTrue();
    }
}