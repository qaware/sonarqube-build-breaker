package de.qaware.tools.sqbb.library.impl;

import de.qaware.tools.sqbb.library.api.BranchMode;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BranchModeParserTest {
    @Test
    void sonarQube() {
        assertThat(new BranchModeParser().parse("sonarQube")).isEqualTo(BranchMode.SONARQUBE);
    }

    @Test
    void projectKey() {
        assertThat(new BranchModeParser().parse("projectKey")).isEqualTo(BranchMode.PROJECT_KEY);
    }

    @Test
    void invalid() {
        assertThatThrownBy(() -> new BranchModeParser().parse("foobar")).isInstanceOf(IllegalArgumentException.class);
    }
}