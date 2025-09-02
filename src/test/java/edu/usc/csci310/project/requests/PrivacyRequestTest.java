package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrivacyRequestTest {
    @Test
    public void getUsernameTest() {
        PrivacyRequest pr = new PrivacyRequest("zoe", true);
        assertEquals("zoe", pr.getUsername());
    }

    @Test
    public void setUsernameTest() {
        PrivacyRequest pr = new PrivacyRequest("zoe", true);
        pr.setUsername("zoe2");
        assertEquals("zoe2", pr.getUsername());
    }

    @Test
    public void getPrivacyTest() {
        PrivacyRequest pr = new PrivacyRequest("zoe", true);
        assertEquals(true, pr.getPrivacy());
    }

    @Test
    public void setPrivacyTest() {
        PrivacyRequest pr = new PrivacyRequest("zoe", true);
        pr.setPrivacy(false);
        assertEquals(false, pr.getPrivacy());
    }
}
