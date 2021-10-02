package de.qaware.tools.sqbb.library.api.connector;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationTest {
    @Test
    void fromToken() {
        assertThat(Authentication.fromToken("token-1").toHttpBasicAuth()).isEqualTo("Basic dG9rZW4tMTo=");
    }

    @Test
    void fromUsernameAndPassword() {
        assertThat(Authentication.fromUsernameAndPassword("user-1", "password-1").toHttpBasicAuth()).isEqualTo("Basic dXNlci0xOnBhc3N3b3JkLTE=");
    }
}