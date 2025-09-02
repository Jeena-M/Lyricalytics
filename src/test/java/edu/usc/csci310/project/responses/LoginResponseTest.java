package edu.usc.csci310.project.responses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginResponseTest {
    @Test
    void setId() {
        LoginResponse loginRes = new LoginResponse(1, "username2", true);
        loginRes.setId(2);
        assertEquals(2, loginRes.getId());
    }

    @Test
    void setUsername() {
        LoginResponse loginRes = new LoginResponse(1, "username2", true);
        loginRes.setUsername("username");
        assertEquals("username", loginRes.getUsername());
    }

    @Test
    void setIsLoggedIn() {
        LoginResponse loginRes = new LoginResponse(1, "username2", false);
        loginRes.setIsLoggedIn(true);
        assertTrue(loginRes.getIsLoggedIn());
    }
}
