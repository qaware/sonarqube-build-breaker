package de.qaware.tools.sqbb.library.api.connector;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Authentication for SonarQube.
 * <p>
 * SonarQube supports two methods of authentication:
 * <p>
 * - username and password
 * - token
 */
public class Authentication {
    private final String username;
    private final String password;

    private Authentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Creates the http basic auth header value from  this authentication object.
     *
     * @return http basic auth header value
     */
    public String toHttpBasicAuth() {
        String value = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates an authentication object from the given token.
     *
     * @param token token
     * @return authentication object
     */
    public static Authentication fromToken(String token) {
        // See https://docs.sonarqube.org/latest/extend/web-api/
        return new Authentication(token, "");
    }

    /**
     * creates an authentication object from the given username and password.
     *
     * @param username username
     * @param password password
     * @return authentication object
     */
    public static Authentication fromUsernameAndPassword(String username, String password) {
        return new Authentication(username, password);
    }
}
