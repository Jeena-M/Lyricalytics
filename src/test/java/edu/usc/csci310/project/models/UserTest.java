package edu.usc.csci310.project.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void getId() {
        User user = new User(1, "Jeena");
        assertEquals(1, user.getId());
    }

    @Test
    void setId() {
        User user = new User(1, "Jeena");
        user.setId(2);
        assertEquals(2, user.getId());
    }

    @Test
    void getUsername() {
        User user = new User(1, "Jeena");
        assertEquals("Jeena", user.getUsername());
    }

    @Test
    void setUsername() {
        User user = new User(1, "Jeena");
        user.setUsername("Parini");
        assertEquals("Parini", user.getUsername());
    }

}