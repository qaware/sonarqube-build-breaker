package de.qaware.tools.sqbb.library.api.http;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * HTTP client which executes HTTP requests.
 */
public interface HttpClient {
    /**
     * Executes a GET request to the given URI.
     *
     * @param uri     uri
     * @param headers headers
     * @return HTTP response
     * @throws IOException if something went wrong while doing I/O
     */
    HttpResponse executeGet(URI uri, Map<String, String> headers) throws IOException;
}
