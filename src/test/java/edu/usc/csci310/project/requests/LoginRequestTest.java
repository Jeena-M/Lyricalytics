package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginRequestTest {
    @Test
    void setLoginRequestUsername() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setLoginRequestUsername("username");
        assertEquals("username", loginReq.getUsername());
    }


    @Test
    void setLoginRequestPassword() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setLoginRequestPassword("password1");
        assertEquals("password1", loginReq.getLoginRequestPassword());
    }

}