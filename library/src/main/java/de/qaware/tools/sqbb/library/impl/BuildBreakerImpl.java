package de.qaware.tools.sqbb.library.impl;

import de.qaware.tools.sqbb.library.api.BreakBuildException;
import de.qaware.tools.sqbb.library.api.BuildBreaker;
import de.qaware.tools.sqbb.library.api.ProjectKey;
import de.qaware.tools.sqbb.library.api.connector.AnalysisTasks;
import de.qaware.tools.sqbb.library.api.connector.QualityGateStatus;
import de.qaware.tools.sqbb.library.api.connector.SonarQubeConnector;
import de.qaware.tools.sqbb.library.api.connector.SonarQubeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

class BuildBreakerImpl implements BuildBreaker {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuildBreakerImpl.class);

    private final Duration waitTime;
    private final SonarQubeConnector sonarQubeConnector;

    /**
     * Constructor.
     *
     * @param waitTime           time to wait between polling the analysis queue
     * @param sonarQubeConnector connector to SonarQube
     */
    BuildBreakerImpl(Duration waitTime, SonarQubeConnector sonarQubeConnector) {
        this.waitTime = waitTime;
        this.sonarQubeConnector = sonarQubeConnector;
    }

    @Override
    public void breakBuildIfNeeded(ProjectKey projectKey) throws IOException, SonarQubeException, InterruptedException, BreakBuildException {
        LOGGER.info("Fetching analysis tasks ...");
        AnalysisTasks analysisTasks = sonarQubeConnector.fetchAnalysisTasks(projectKey);
        while (!analysisTasks.getQueue().isEmpty()) {
            LOGGER.info("Analysis task still running, checking again in {} second(s) ...", waitTime.toMillis() / 1000);
            Thread.sleep(waitTime.toMillis());
            analysisTasks = sonarQubeConnector.fetchAnalysisTasks(projectKey);
        }

        if (analysisTasks.getLastFinished() == null) {
            LOGGER.error("Analysis queue is empty and there is no finished task. Make sure that you run SonarQube analysis before the build breaker!");
            throw new BreakBuildException("Analysis queue is empty and there is no finished task. Make sure that you run SonarQube analysis before the build breaker!");
        }

        if (analysisTasks.getLastFinished().getStatus() != AnalysisTasks.Status.SUCCESS) {
            LOGGER.error("Last analysis task failed, breaking build!");
            throw new BreakBuildException("Last analysis task failed, breaking build!");
        }

        LOGGER.info("Last analysis was successful, fetching quality gate status ...");
        QualityGateStatus qualityGateStatus = sonarQubeConnector.fetchQualityGateStatus(projectKey);

        if (qualityGateStatus == QualityGateStatus.ERROR) {
            LOGGER.error("Quality gate failed, breaking build!");
            throw new BreakBuildException("Quality gate failed, breaking build!");
        }

        LOGGER.info("Great success, everything looks alright!");
    }
}
