package de.qaware.tools.sqbb.library.api.connector;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationTest {
    @Test
    void toBasicAuth() {
        assertThat(Authentication.fromToken("token-1").toHttpBasicAuth()).isEqualTo("Basic dG9rZW4tMTo=");
    }
}