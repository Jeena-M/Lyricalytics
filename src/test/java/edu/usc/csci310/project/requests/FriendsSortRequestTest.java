package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FriendsSortRequestTest {
    @Test
    public void setUsernameTest() {
        FriendsSortRequest req = new FriendsSortRequest(true, "user2");
        req.setUsername("user3");
        assertEquals("user3", req.getUsername());
    }

    @Test
    public void setAscendingTest() {
        FriendsSortRequest req = new FriendsSortRequest(true, "user2");
        req.setAscending(false);
        assertEquals(false, req.getAscending());
    }
}
