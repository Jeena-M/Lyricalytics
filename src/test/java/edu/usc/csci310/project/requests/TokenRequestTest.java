package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TokenRequestTest {
    private HttpClient client;
    private HttpResponse<String> response;
    private TokenRequest tokenRequest;

    @BeforeEach
    public void setUp() {
        client = mock(HttpClient.class);
        response = mock(HttpResponse.class);
        tokenRequest = new TokenRequest(client);
    }

    @Test
    public void testConstructor() {
        TokenRequest tokenReq = new TokenRequest();
        assertTrue(tokenReq != null);
    }

    @Test
    public void testGetAccessToken() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"access_token\": \"fake_access_token123\"}");
        assertEquals("fake_access_token123", tokenRequest.getAccessToken());
    }

    @Test
    public void testGetAccessTokenFails() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(400);
        assertThrows(Exception.class, () -> tokenRequest.getAccessToken());
    }
}