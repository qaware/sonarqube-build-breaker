package de.qaware.tools.sqbb.library.impl;

import de.qaware.tools.sqbb.library.api.BreakBuildException;
import de.qaware.tools.sqbb.library.api.ProjectKey;
import de.qaware.tools.sqbb.library.api.connector.AnalysisTasks;
import de.qaware.tools.sqbb.library.api.connector.QualityGateStatus;
import de.qaware.tools.sqbb.library.api.connector.SonarQubeConnector;
import de.qaware.tools.sqbb.library.api.connector.SonarQubeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BuildBreakerImplTest {
    private static final ProjectKey PROJECT_KEY = ProjectKey.of("project-1");
    private SonarQubeConnector sonarQubeConnector;
    private BuildBreakerImpl sut;

    @BeforeEach
    void setUp() {
        sonarQubeConnector = mock(SonarQubeConnector.class);
        sut = new BuildBreakerImpl(Duration.ofSeconds(1), sonarQubeConnector);
    }

    @Test
    void no_analysis() throws InterruptedException, SonarQubeException, BreakBuildException, IOException {
        // Given: no analysis and nothing in the queue
        setupAnalysisTasks(null);

        // When: we execute the build breaker
        // Then: we break the build with a useful message
        assertThatThrownBy(() -> sut.breakBuildIfNeeded(PROJECT_KEY)).isInstanceOf(BreakBuildException.class).hasMessageContaining("run SonarQube analysis before the build breaker");
    }

    @Test
    void failed_analysis() throws IOException, SonarQubeException {
        // Given: a failed analysis and nothing in the queue
        setupAnalysisTasks(new AnalysisTasks.Task(AnalysisTasks.Status.FAILED));

        // When: we execute the build breaker
        // Then: we break the build with a useful message
        assertThatThrownBy(() -> sut.breakBuildIfNeeded(PROJECT_KEY)).isInstanceOf(BreakBuildException.class).hasMessageContaining("analysis task failed");
    }

    @Test
    void wait_for_queue() throws IOException, SonarQubeException {
        // Given: on the 1st call an analysis in the queue, on the 2nd call a finished analysis
        when(sonarQubeConnector.fetchAnalysisTasks(PROJECT_KEY)).thenReturn(
            new AnalysisTasks(Collections.singletonList(new AnalysisTasks.Task(AnalysisTasks.Status.PENDING)), null),
            new AnalysisTasks(Collections.emptyList(), new AnalysisTasks.Task(AnalysisTasks.Status.FAILED))
        );

        // When: we execute the build breaker
        // Then: we break the build with a useful message
        assertThatThrownBy(() -> sut.breakBuildIfNeeded(PROJECT_KEY)).isInstanceOf(BreakBuildException.class).hasMessageContaining("analysis task failed");
    }

    @Test
    void quality_gate_failed() throws IOException, SonarQubeException {
        // Given: success analysis and nothing in the queue
        setupAnalysisTasks(new AnalysisTasks.Task(AnalysisTasks.Status.SUCCESS));
        // Given: quality gate is error
        when(sonarQubeConnector.fetchQualityGateStatus(PROJECT_KEY)).thenReturn(QualityGateStatus.ERROR);

        // When: we execute the build breaker
        // Then: we break the build with a useful message
        assertThatThrownBy(() -> sut.breakBuildIfNeeded(PROJECT_KEY)).isInstanceOf(BreakBuildException.class).hasMessageContaining("Quality gate failed");
    }

    @Test
    void quality_gate_ok() throws IOException, SonarQubeException, BreakBuildException, InterruptedException {
        // Given: success analysis and nothing in the queue
        setupAnalysisTasks(new AnalysisTasks.Task(AnalysisTasks.Status.SUCCESS));
        // Given: quality gate is error
        when(sonarQubeConnector.fetchQualityGateStatus(PROJECT_KEY)).thenReturn(QualityGateStatus.OK);

        // When: we execute the build breaker
        // Then: everything is fine
        assertThatCode(() -> {
            sut.breakBuildIfNeeded(PROJECT_KEY);
        }).doesNotThrowAnyException();
    }

    private void setupAnalysisTasks(@Nullable AnalysisTasks.Task current, AnalysisTasks.Task... queue) throws IOException, SonarQubeException {
        when(sonarQubeConnector.fetchAnalysisTasks(PROJECT_KEY)).thenReturn(new AnalysisTasks(Arrays.asList(queue), current));
    }
}