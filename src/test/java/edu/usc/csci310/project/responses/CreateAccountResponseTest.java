package edu.usc.csci310.project.responses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateAccountResponseTest {

    @Test
    void getId() {
        CreateAccountResponse response = new CreateAccountResponse(1, "Jeena", "Login123");
        assertEquals(1, response.getIdAccount());
    }

    @Test
    void setId() {
        CreateAccountResponse response = new CreateAccountResponse(1, "Jeena", "Login123");
        response.setIdAccount(2);
        assertEquals(2, response.getIdAccount());
    }

    @Test
    void getUsername() {
        CreateAccountResponse response = new CreateAccountResponse(1, "Jeena", "Login123");
        assertEquals("Jeena", response.getUsernameAccount());
    }

    @Test
    void setCreateAccountUsername() {
        CreateAccountResponse response = new CreateAccountResponse(1, "Jeena", "Login123");
        response.setUsernameAccount("Parini");
        assertEquals("Parini", response.getUsernameAccount());
    }

    @Test
    void getCreateAccountPassword() {
        CreateAccountResponse response = new CreateAccountResponse(1, "Jeena", "Login123");
        assertEquals("Login123", response.getPasswordAccount());
    }

    @Test
    void setCreateAccountPassword() {
        CreateAccountResponse response = new CreateAccountResponse(1, "Jeena", "Login123");
        response.setPasswordAccount("Login1234");
        assertEquals("Login1234", response.getPasswordAccount());
    }
}