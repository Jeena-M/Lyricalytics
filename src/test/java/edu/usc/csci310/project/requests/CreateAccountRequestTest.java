package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateAccountRequestTest {

    @Test
    void getUsername() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setUsername("username");
        assertEquals("username", request.getUsername());
    }

    @Test
    void setUsername() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setUsername("username");
        assertEquals("username", request.getUsername());
    }

    @Test
    void getPassword() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setPassword("Login123");
        assertEquals("Login123", request.getPassword());
    }

    @Test
    void setPassword() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setPassword("Login123");
        assertEquals("Login123", request.getPassword());
    }

    @Test
    void getConfirmPassword() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setConfirmPassword("Login123");
        assertEquals("Login123", request.getConfirmPassword());
    }

    @Test
    void setConfirmPassword() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setConfirmPassword("Login123");
        assertEquals("Login123", request.getConfirmPassword());
    }
}