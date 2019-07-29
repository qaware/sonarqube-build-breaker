package de.qaware.tools.sqbb.library.impl.connector;

import de.qaware.tools.sqbb.library.api.ProjectKey;
import de.qaware.tools.sqbb.library.api.connector.AnalysisTasks;
import de.qaware.tools.sqbb.library.api.connector.Authentication;
import de.qaware.tools.sqbb.library.api.connector.ProjectNotFoundException;
import de.qaware.tools.sqbb.library.api.connector.QualityGateStatus;
import de.qaware.tools.sqbb.library.api.connector.SonarQubeConnector;
import de.qaware.tools.sqbb.library.api.connector.SonarQubeException;
import de.qaware.tools.sqbb.library.api.http.HttpClient;
import de.qaware.tools.sqbb.library.api.http.HttpResponse;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SonarQubeConnectorImplTest {
    private static final ProjectKey PROJECT_KEY = ProjectKey.of("project-1");

    private SonarQubeConnector sut;
    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = mock(HttpClient.class);

        sut = new SonarQubeConnectorImpl(httpClient, "http://localhost:9000", Authentication.fromToken("token-1"));
    }

    @Test
    void fetchAnalysisTasks_authentication_failed() throws IOException, SonarQubeException {
        // Given: Return 401 on every http call
        when(httpClient.executeGet(any(), any())).thenReturn(new HttpResponse(401, "Authentication failed"));

        // When: We fetch the tasks
        // Then: we get an exception with a useful message
        assertThatThrownBy(() -> sut.fetchAnalysisTasks(PROJECT_KEY)).isInstanceOf(SonarQubeException.class).hasMessageContaining("got 401");
    }

    @Test
    void fetchAnalysisTasks_project_not_found() throws IOException, SonarQubeException {
        // Given: Return 404 on every http call
        when(httpClient.executeGet(any(), any())).thenReturn(new HttpResponse(404, "Project not found"));

        // When: We fetch the tasks
        // Then: we get an ProjectNotFoundException
        assertThatThrownBy(() -> sut.fetchAnalysisTasks(PROJECT_KEY)).isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void fetchQualityGateStatus_authentication_failed() throws IOException, SonarQubeException {
        // Given: Return 401 on every http call
        when(httpClient.executeGet(any(), any())).thenReturn(new HttpResponse(401, "Authentication failed"));

        // When: We fetch the quality gate status
        // Then: we get an exception with a useful message
        assertThatThrownBy(() -> sut.fetchQualityGateStatus(PROJECT_KEY)).isInstanceOf(SonarQubeException.class).hasMessageContaining("got 401");
    }

    @Test
    void fetchQualityGateStatus_project_not_found() throws IOException, SonarQubeException {
        // Given: Return 404 on every http call
        when(httpClient.executeGet(any(), any())).thenReturn(new HttpResponse(404, "Project not found"));

        // When: We fetch the quality gate status
        // Then: we get an ProjectNotFoundException
        assertThatThrownBy(() -> sut.fetchQualityGateStatus(PROJECT_KEY)).isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void fetchAnalysisTasks_success() throws IOException, SonarQubeException {
        // Given: Return mock content on http call
        httpReturns("http://localhost:9000/api/ce/component?component=project-1", 200, "/response/fetchAnalysisTasks_success.json");

        // When: We fetch the analysis tasks
        AnalysisTasks tasks = sut.fetchAnalysisTasks(PROJECT_KEY);

        // Then: the queue is empty, and the last state is success
        assertThat(tasks.getQueue()).isEmpty();
        assertThat(tasks.getLastFinished().getStatus()).isEqualTo(AnalysisTasks.Status.SUCCESS);
    }

    @Test
    void fetchAnalysisTasks_failed() throws IOException, SonarQubeException {
        // Given: Return mock content on http call
        httpReturns("http://localhost:9000/api/ce/component?component=project-1", 200, "/response/fetchAnalysisTasks_failed.json");

        // When: We fetch the analysis tasks
        AnalysisTasks tasks = sut.fetchAnalysisTasks(PROJECT_KEY);

        // Then: the queue is empty, and the last state is success
        assertThat(tasks.getQueue()).isEmpty();
        assertThat(tasks.getLastFinished().getStatus()).isEqualTo(AnalysisTasks.Status.FAILED);
    }

    @Test
    void fetchAnalysisTasks_queue() throws IOException, SonarQubeException {
        // Given: Return mock content on http call
        httpReturns("http://localhost:9000/api/ce/component?component=project-1", 200, "/response/fetchAnalysisTasks_queue.json");

        // When: We fetch the analysis tasks
        AnalysisTasks tasks = sut.fetchAnalysisTasks(PROJECT_KEY);

        // Then: the queue is empty, and the last state is success
        assertThat(tasks.getQueue()).hasSize(1);
        assertThat(tasks.getQueue().get(0).getStatus()).isEqualTo(AnalysisTasks.Status.PENDING);
        assertThat(tasks.getLastFinished().getStatus()).isEqualTo(AnalysisTasks.Status.SUCCESS);
    }

    @Test
    void fetchAnalysisTasks_no_current() throws IOException, SonarQubeException {
        // Given: Return mock content on http call
        httpReturns("http://localhost:9000/api/ce/component?component=project-1", 200, "/response/fetchAnalysisTasks_no_current.json");

        // When: We fetch the analysis tasks
        AnalysisTasks tasks = sut.fetchAnalysisTasks(PROJECT_KEY);

        // Then: the queue is empty, and the last state is success
        assertThat(tasks.getQueue()).isEmpty();
        assertThat(tasks.getLastFinished()).isNull();
    }

    @Test
    void fetchQualityGateStatus_ok() throws IOException, SonarQubeException {
        // Given: Return mock content on http call
        httpReturns("http://localhost:9000/api/qualitygates/project_status?projectKey=project-1", 200, "/response/fetchQualityGateStatus_ok.json");

        // When: We fetch the quality gate status
        QualityGateStatus status = sut.fetchQualityGateStatus(PROJECT_KEY);

        // Then: the status is OK
        assertThat(status).isEqualTo(QualityGateStatus.OK);
    }

    @Test
    void fetchQualityGateStatus_error() throws IOException, SonarQubeException {
        // Given: Return mock content on http call
        httpReturns("http://localhost:9000/api/qualitygates/project_status?projectKey=project-1", 200, "/response/fetchQualityGateStatus_error.json");

        // When: We fetch the quality gate status
        QualityGateStatus status = sut.fetchQualityGateStatus(PROJECT_KEY);

        // Then: the status is ERROR
        assertThat(status).isEqualTo(QualityGateStatus.ERROR);
    }

    private void httpReturns(String uri, int code, String resource) throws IOException {
        String content = IOUtils.resourceToString(resource, StandardCharsets.UTF_8);
        when(httpClient.executeGet(URI.create(uri), authentication())).thenReturn(new HttpResponse(code, content));
    }

    private Map<String, String> authentication() {
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", "Basic dG9rZW4tMTo=");
        return headers;
    }
}