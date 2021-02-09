package de.qaware.tools.sqbb.library.impl;

import de.qaware.tools.sqbb.library.api.BuildBreaker;
import de.qaware.tools.sqbb.library.api.connector.Authentication;
import de.qaware.tools.sqbb.library.api.connector.SonarQubeConnector;
import de.qaware.tools.sqbb.library.impl.connector.SonarQubeConnectorImpl;
import de.qaware.tools.sqbb.library.impl.http.ApacheHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.time.Duration;

/**
 * Factory for {@link BuildBreaker} instances.
 */
public final class BuildBreakerFactory {
    private BuildBreakerFactory() {
    }

    /**
     * Creates a new {@link BuildBreaker} instance. Make sure you call the close() method of the returned instance when
     * done.
     *
     * @param waitTime       time to wait between polling the analysis queue
     * @param baseUrl        SonarQube base url
     * @param authentication authentication for SonarQube
     * @return a BuildBreaker instance
     */
    public static CloseableBuildBreaker create(Duration waitTime, String baseUrl, Authentication authentication) {
        CloseableHttpClient httpClient = HttpClients.createSystem();
        SonarQubeConnector sonarQubeConnector = new SonarQubeConnectorImpl(new ApacheHttpClient(httpClient), baseUrl, authentication);
        BuildBreakerImpl buildBreaker = new BuildBreakerImpl(waitTime, sonarQubeConnector);

        return new CloseableBuildBreaker() {
            @Override
            public BuildBreaker get() {
                return buildBreaker;
            }

            @Override
            public void close() throws Exception {
                httpClient.close();
            }
        };
    }

    /**
     * A {@link BuildBreaker} which is closeable.
     */
    public interface CloseableBuildBreaker extends AutoCloseable {
        /**
         * Gets the {@link BuildBreaker}.
         *
         * @return the BuildBreaker
         */
        BuildBreaker get();
    }
}
