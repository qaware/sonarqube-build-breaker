package de.qaware.tools.sqbb.library.impl.http;

import de.qaware.tools.sqbb.library.api.http.HttpClient;
import de.qaware.tools.sqbb.library.api.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Uses the Apache HTTP client for http communication.
 */
public class ApacheHttpClient implements HttpClient {
    private final CloseableHttpClient httpClient;

    public ApacheHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public HttpResponse executeGet(URI uri, Map<String, String> headers) throws IOException {
        HttpGet request = new HttpGet(uri);
        for (Map.Entry<String, String> header : headers.entrySet()) {
            request.addHeader(header.getKey(), header.getValue());
        }

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String content = EntityUtils.toString(response.getEntity());
            return new HttpResponse(response.getStatusLine().getStatusCode(), content);
        }
    }
}
