package edu.usc.csci310.project.controllers;

import edu.usc.csci310.project.models.User;
import edu.usc.csci310.project.requests.CreateAccountRequest;
import edu.usc.csci310.project.requests.LoginRequest;
import edu.usc.csci310.project.responses.CreateAccountResponse;
import edu.usc.csci310.project.responses.LoginResponse;
import edu.usc.csci310.project.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Test
    void isValidUsernameSuccess() {
        UserService service = mock();
        UserController controller = new UserController(service);
        String password = "Login123";
        assertTrue(controller.isValidPassword(password));
    }

    @Test
    void isValidUsernameFailure() {
        UserService service = mock();
        UserController controller = new UserController(service);
        String password = "login123";
        assertFalse(controller.isValidPassword(password));
    }

    @Test
    void createAccountSuccess() throws SQLException {
        UserService service = mock(); // Return a fake UserService object
        UserController controller = new UserController(service);

        CreateAccountRequest request = new CreateAccountRequest();
        request.setUsername("Jeena");
        request.setPassword("Login123");
        request.setConfirmPassword("Login123");

        when(service.createAccount(request)).thenReturn(1);

        ResponseEntity<CreateAccountResponse> response = controller.createAccount(request);

        assertEquals(1, response.getBody().getIdAccount());
        assertEquals("Jeena", response.getBody().getUsernameAccount());
        assertEquals("Login123", response.getBody().getPasswordAccount());
    }

    @Test
    void createAccountInvalidPassword() throws SQLException {
        UserService service = mock(); // Return a fake UserService object
        UserController controller = new UserController(service);

        CreateAccountRequest request = new CreateAccountRequest();
        request.setUsername("Jeena");
        request.setPassword("login123");
        request.setConfirmPassword("login123");

        assertEquals(controller.createAccount(request).getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void createAccountPasswordMismatch() throws SQLException {
        UserService service = mock(); // Return a fake UserService object
        UserController controller = new UserController(service);

        CreateAccountRequest request = new CreateAccountRequest();
        request.setUsername("Jeena");
        request.setPassword("Login123");
        request.setConfirmPassword("Login1234");

        assertEquals(controller.createAccount(request).getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void createAccountFailed() throws SQLException {
        UserService service = mock(); // Return a fake UserService object
        UserController controller = new UserController(service);

        CreateAccountRequest request = new CreateAccountRequest();
        request.setUsername("Jeena");
        request.setPassword("Login123");
        request.setConfirmPassword("Login123");

        SQLException exception = new SQLException("ERROR");
        when(service.createAccount(request)).thenThrow(exception);

        assertEquals(controller.createAccount(request).getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void createAccountUsernameExists() throws SQLException {
        UserService service = mock(); // Return a fake UserService object
        UserController controller = new UserController(service);

        CreateAccountRequest request = new CreateAccountRequest();
        request.setUsername("Jeena");
        request.setPassword("Login123");
        request.setConfirmPassword("Login123");

        when(service.doesUsernameExist(request.getUsername())).thenThrow(new SQLException("Username already exists"));


        assertEquals(controller.createAccount(request).getStatusCode(), HttpStatus.BAD_REQUEST);
    }


    @Test
    void loginSuccessful() throws SQLException {
        UserService us = mock(UserService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);

        UserController uc = new UserController(us);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLoginRequestUsername("Zoe");
        loginRequest.setLoginRequestPassword("Password1");

        when(us.correctUsernameAndPassword("Zoe", "Password1")).thenReturn(true);
        User u = new User(2, "Zoe");
        when(us.getUser("Zoe")).thenReturn(u);

        ResponseEntity<LoginResponse> response = uc.login(loginRequest, request);

        verify(request, times(1)).getSession(true);
        verify(session, times(1)).setMaxInactiveInterval(60);
        verify(session, times(1)).setAttribute("user", u);

        assertEquals(2, response.getBody().getId());
        assertEquals("Zoe", response.getBody().getUsername());
        assertTrue(response.getBody().getIsLoggedIn());
    }

    @Test
    void loginWrongUsername() throws SQLException {
        UserService us = mock(UserService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);

        UserController uc = new UserController(us);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLoginRequestUsername("Zoe1");
        loginRequest.setLoginRequestPassword("Password1");

        // Set service expectation: wrong username or password
        when(us.correctUsernameAndPassword("Zoe1", "Password1")).thenReturn(false);

        ResponseEntity<LoginResponse> response = uc.login(loginRequest, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void loginWrongPassword() throws SQLException {
        UserService us = mock(UserService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);

        UserController uc = new UserController(us);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLoginRequestUsername("Zoe");
        loginRequest.setLoginRequestPassword("Password2");

        when(us.correctUsernameAndPassword("Zoe", "Password2")).thenReturn(false);

        ResponseEntity<LoginResponse> response = uc.login(loginRequest, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void loginSQLError() throws SQLException {
        UserService us = mock(UserService.class);
        UserController uc = new UserController(us);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLoginRequestUsername("");
        loginRequest.setLoginRequestPassword("Password1");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);

        when(us.correctUsernameAndPassword("", "Password1")).thenThrow(new SQLException("SQL Error"));

        ResponseEntity<LoginResponse> response = uc.login(loginRequest, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void loginUserAccountLocked() throws SQLException {
        UserService us = mock(UserService.class);
        UserController uc = new UserController(us);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLoginRequestUsername("LockedUser");
        loginRequest.setLoginRequestPassword("AnyPassword");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(us.doesUsernameExist("LockedUser")).thenReturn(true);
        when(us.isAccountLocked("LockedUser")).thenReturn(true);

        ResponseEntity<LoginResponse> response = uc.login(loginRequest, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Account locked due to multiple failed login attempts. Please try again later.",
                response.getHeaders().getFirst("X-Error-Message"));
        assertNull(response.getBody());
    }

    @Test
    void loginFailAndAccountBecomesLocked() throws SQLException {
        UserService us = mock(UserService.class);
        UserController uc = new UserController(us);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLoginRequestUsername("Zoe");
        loginRequest.setLoginRequestPassword("WrongPassword");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);

        when(us.doesUsernameExist("Zoe")).thenReturn(true);
        when(us.isAccountLocked("Zoe")).thenReturn(false);

        when(us.correctUsernameAndPassword("Zoe", "WrongPassword")).thenReturn(false);
        when(us.recordFailedAttempt("Zoe")).thenReturn(true);

        ResponseEntity<LoginResponse> response = uc.login(loginRequest, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Account locked due to multiple failed login attempts. Please try again in 5 minutes.",
                response.getHeaders().getFirst("X-Error-Message"));
        assertNull(response.getBody());
    }

    @Test
    void loginFailAndAccountNotLockedYet() throws SQLException {
        UserService us = mock(UserService.class);
        UserController uc = new UserController(us);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLoginRequestUsername("Zoe");
        loginRequest.setLoginRequestPassword("WrongPasswordAgain");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);

        when(us.doesUsernameExist("Zoe")).thenReturn(true);
        when(us.isAccountLocked("Zoe")).thenReturn(false);

        when(us.correctUsernameAndPassword("Zoe", "WrongPasswordAgain")).thenReturn(false);
        when(us.recordFailedAttempt("Zoe")).thenReturn(false);

        ResponseEntity<LoginResponse> response = uc.login(loginRequest, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username or password is incorrect",
                response.getHeaders().getFirst("X-Error-Message"));
        assertNull(response.getBody());
    }

    @Test
    void createAccountUsernameNull() throws SQLException {
        UserService service = mock(); // Return a fake UserService object
        UserController controller = new UserController(service);

        CreateAccountRequest request = new CreateAccountRequest();
        request.setPassword("Login123");
        request.setConfirmPassword("Login123");

        assertEquals(HttpStatus.BAD_REQUEST, controller.createAccount(request).getStatusCode());
    }

    @Test
    void createAccountUsernameEmpty() throws SQLException {
        UserService service = mock(); // Return a fake UserService object
        UserController controller = new UserController(service);

        CreateAccountRequest request = new CreateAccountRequest();
        request.setUsername("");
        request.setPassword("Login123");
        request.setConfirmPassword("Login123");

        assertEquals(HttpStatus.BAD_REQUEST, controller.createAccount(request).getStatusCode());
    }

    @Test
    void createAccount_usernameAlreadyExists_errorHeaderIncluded() throws SQLException {
        UserService service = mock();
        UserController controller = new UserController(service);

        CreateAccountRequest request = new CreateAccountRequest();
        request.setUsername("Jeena");
        request.setPassword("Login123");
        request.setConfirmPassword("Login123");

        when(service.doesUsernameExist("Jeena")).thenReturn(true);

        ResponseEntity<CreateAccountResponse> response = controller.createAccount(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getHeaders().getFirst("X-Error-Message"));
        assertNull(response.getBody());
    }

}