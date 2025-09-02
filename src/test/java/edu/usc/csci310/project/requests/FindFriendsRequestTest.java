package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindFriendsRequestTest {
    @Test
    public void setUsernameTest() {
        FindFriendsRequest req = new FindFriendsRequest();
        req.setUsername("user3");
        assertEquals("user3", req.getUsername());
    }

    @Test
    public void setFriendnameTest() {
        FindFriendsRequest req = new FindFriendsRequest("user1", "user2");
        req.setFriendname("user3");
        assertEquals("user3", req.getFriendname());
    }
}
