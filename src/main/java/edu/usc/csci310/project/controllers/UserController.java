package edu.usc.csci310.project.controllers;

import edu.usc.csci310.project.models.User;
import edu.usc.csci310.project.requests.CreateAccountRequest;
import edu.usc.csci310.project.requests.LoginRequest;
import edu.usc.csci310.project.responses.CreateAccountResponse;
import edu.usc.csci310.project.responses.LoginResponse;
import edu.usc.csci310.project.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public boolean isValidPassword (String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]+$");
    }

    @PostMapping("/register")
    public ResponseEntity<CreateAccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        try {

            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .header("X-Error-Message", "Username can't be empty")
                        .body(null);
            }

            if (!isValidPassword(request.getPassword())) {

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .header("X-Error-Message", "Invalid password: password needs to contain at least 1 uppercase and lowercase letter and a number.")
                        .body(null);
            }

            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .header("X-Error-Message", "Passwords do not match")
                        .body(null);
            }

            if (userService.doesUsernameExist(request.getUsername())){
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .header("X-Error-Message", "Username already exists")
                        .body(null);
            }
            int id = userService.createAccount(request);
            CreateAccountResponse response = new CreateAccountResponse(id, request.getUsername(), request.getPassword());
            return ResponseEntity.ok().body(response);
        } catch (SQLException e) {
            if ("Username already exists".equals(e.getMessage())) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .header("X-Error-Message", "Username already exists")
                        .body(null);
            }
            // fallback to 500
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Error adding user: " + e.getMessage())
                    .body(null);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {

        try {
            if (userService.doesUsernameExist(loginRequest.getUsername())) {
                if (userService.isAccountLocked(loginRequest.getUsername())) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .header("X-Error-Message", "Account locked due to multiple failed login attempts. Please try again later.")
                            .body(null);
                }
            }

            if (!userService.correctUsernameAndPassword(loginRequest.getUsername(), loginRequest.getLoginRequestPassword())) {
                boolean locked = userService.recordFailedAttempt(loginRequest.getUsername());
                if (locked) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .header("X-Error-Message", "Account locked due to multiple failed login attempts. Please try again in 5 minutes.")
                            .body(null);
                } else {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .header("X-Error-Message", "Username or password is incorrect")
                            .body(null);
                }
            }

            userService.resetFailedAttempts(loginRequest.getUsername());
            User u = userService.getUser(loginRequest.getUsername());
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(60); // Session expires after 60 seconds of inactivity
            session.setAttribute("user", u);
            LoginResponse response = new LoginResponse(u.getId(), u.getUsername(), true);
            return ResponseEntity.ok().body(response);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Error logging in: " + e.getMessage())
                    .body(null);
        }
    }
}