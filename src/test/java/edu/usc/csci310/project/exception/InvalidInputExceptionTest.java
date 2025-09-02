package edu.usc.csci310.project.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidInputExceptionTest {
    @Test
    public void testInvalidInputException() {
        String message = "Invalid input";

        // Instantiate InvalidInputException with given error message
        InvalidInputException exception = new InvalidInputException(message);

        assertNotNull(exception);  // Verify exception
        assertEquals(message, exception.getMessage());  // Verify message
    }
}