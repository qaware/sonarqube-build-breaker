package de.qaware.tools.sqbb.library.api.http;

/**
 * HTTP response from the {@link HttpClient}.
 */
public class HttpResponse {
    /**
     * Status code.
     * <p>
     * 200 for OK, etc.
     */
    private final int code;
    /**
     * Content of the HTTP response.
     */
    private final String content;

    /**
     * Ctor.
     *
     * @param code    status code
     * @param content content
     */
    public HttpResponse(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public String getContent() {
        return content;
    }
}
