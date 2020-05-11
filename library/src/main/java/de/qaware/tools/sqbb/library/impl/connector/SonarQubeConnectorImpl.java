package de.qaware.tools.sqbb.library.impl.connector;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qaware.tools.sqbb.library.api.BranchMode;
import de.qaware.tools.sqbb.library.api.ProjectKey;
import de.qaware.tools.sqbb.library.api.connector.AnalysisTasks;
import de.qaware.tools.sqbb.library.api.connector.Authentication;
import de.qaware.tools.sqbb.library.api.connector.ProjectNotFoundException;
import de.qaware.tools.sqbb.library.api.connector.QualityGateStatus;
import de.qaware.tools.sqbb.library.api.connector.SonarQubeConnector;
import de.qaware.tools.sqbb.library.api.connector.SonarQubeException;
import de.qaware.tools.sqbb.library.api.http.HttpClient;
import de.qaware.tools.sqbb.library.api.http.HttpResponse;
import de.qaware.tools.sqbb.library.impl.connector.model.AnalysisTasksDto;
import de.qaware.tools.sqbb.library.impl.connector.model.QualityGateDto;
import de.qaware.tools.sqbb.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation for the {@link SonarQubeConnector}.
 */
public class SonarQubeConnectorImpl implements SonarQubeConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(SonarQubeConnectorImpl.class);
    private static final int NOT_FOUND = 404;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final Map<String, String> httpHeaders;

    /**
     * Constructor.
     *
     * @param httpClient     http client
     * @param baseUrl        SonarQube base url
     * @param authentication authentication for SonarQube
     */
    public SonarQubeConnectorImpl(HttpClient httpClient, String baseUrl, Authentication authentication) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.httpHeaders = createHttpHeaders(authentication);
        this.objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public QualityGateStatus fetchQualityGateStatus(ProjectKey projectKey, BranchMode branchMode) throws IOException, SonarQubeException {
        LOGGER.debug("Fetching quality gate status for project {}", projectKey);
        StringBuilder uriString =
            new StringBuilder("api/qualitygates/project_status?projectKey=")
                .append(queryValue(projectKeyToString(projectKey, branchMode)));

        if (projectKey.getBranch() != null && branchMode == BranchMode.SONARQUBE) {
            uriString.append("&branch=").append(queryValue(projectKey.getBranch()));
        }

        URI uri = resolveUri(uriString.toString());

        String content = executeGet(uri);
        QualityGateDto dto = objectMapper.readValue(content, QualityGateDto.class);

        return QualityGateStatus.fromSonar(dto.getProjectStatus().getStatus());
    }

    @Override
    public AnalysisTasks fetchAnalysisTasks(ProjectKey projectKey, BranchMode branchMode) throws IOException, SonarQubeException {
        LOGGER.debug("Fetching analysis tasks for project {}", projectKey);
        URI uri = resolveUri("api/ce/component?component=" + queryValue(projectKeyToString(projectKey, branchMode)));

        String content = executeGet(uri);
        AnalysisTasksDto dto = objectMapper.readValue(content, AnalysisTasksDto.class);

        return new AnalysisTasks(Lists.map(dto.getQueue(), AnalysisTasksDto.TaskDto::toDto), dto.getCurrent() == null ? null : dto.getCurrent().toDto());
    }

    private Map<String, String> createHttpHeaders(Authentication authentication) {
        Map<String, String> result = new HashMap<>(1);
        result.put("Authorization", authentication.toHttpBasicAuth());
        return Collections.unmodifiableMap(result);
    }

    private String queryValue(String key) throws UnsupportedEncodingException {
        return URLEncoder.encode(key, "UTF-8");
    }

    private URI resolveUri(String path) {
        if (baseUrl.endsWith("/")) {
            return URI.create(baseUrl + path);
        } else {
            return URI.create(baseUrl + "/" + path);
        }
    }

    private String executeGet(URI uri) throws IOException, SonarQubeException {
        LOGGER.trace("GET {}", uri);
        HttpResponse response = httpClient.executeGet(uri, httpHeaders);

        LOGGER.trace("Got content: '{}'", response.getContent());
        checkStatus(response);
        return response.getContent();
    }

    private void checkStatus(HttpResponse response) throws SonarQubeException {
        LOGGER.trace("Got status {}", response.getCode());

        if (response.getCode() == NOT_FOUND) {
            throw new ProjectNotFoundException(response.getContent(), "Project not found");
        }

        if (response.getCode() / 100 != 2) {
            throw new SonarQubeException(response.getContent(), "Expected status code 2xx, got " + response.getCode());
        }
    }

    private String projectKeyToString(ProjectKey projectKey, BranchMode branchMode) {
        switch (branchMode) {
            case PROJECT_KEY:
                if (projectKey.getBranch() == null) {
                    return projectKey.getKey();
                }
                return projectKey.getKey() + ":" + projectKey.getBranch();
            case SONARQUBE:
                return projectKey.getKey();
            default:
                throw new IllegalStateException("Unexpected value: " + branchMode);
        }
    }
}
