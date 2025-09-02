package edu.usc.csci310.project.controllers;

import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.requests.FindFriendsRequest;
import edu.usc.csci310.project.requests.SongDetailsFriendsRequest;
import edu.usc.csci310.project.services.FriendsService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FriendsControllerTest {

    @Test
    void getFriends_success() throws Exception {
        FriendsService service = mock();
        FriendsController controller = new FriendsController(service);

        Map<String, List<String>> mockResponse = new HashMap<>();
        mockResponse.put("song1", List.of("Zoe", "Jeena"));

        FindFriendsRequest request = new FindFriendsRequest();
        request.setFriendname("Zoe");
        request.setUsername("Jeena");

        when(service.getComparisonList("Zoe", "Jeena")).thenReturn(mockResponse);

        ResponseEntity<Map<String, List<String>>> response = controller.getFriends(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void getFriends_exceptionThrown_returnsBadRequest() throws Exception {
        FriendsService service = mock();
        FriendsController controller = new FriendsController(service);

        FindFriendsRequest request = new FindFriendsRequest();
        request.setFriendname("Zoe");
        request.setUsername("Jeena");

        when(service.getComparisonList("Zoe", "Jeena")).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<Map<String, List<String>>> response = controller.getFriends(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("_error"));
        assertEquals("User not found", response.getBody().get("_error").get(0));
    }

    @Test
    void clearComparisonMap_usernamePresent_callsService() {
        FriendsService service = mock();
        FriendsController controller = new FriendsController(service);

        Map<String, String> payload = new HashMap<>();
        payload.put("username", "jeena");

        ResponseEntity<Void> response = controller.clearComparisonMap(payload);

        verify(service).clearComparisonMapForUser("jeena");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void clearComparisonMap_usernameMissing_doesNotCallService() {
        FriendsService service = mock();
        FriendsController controller = new FriendsController(service);

        Map<String, String> payload = new HashMap<>();

        ResponseEntity<Void> response = controller.clearComparisonMap(payload);

        verify(service, never()).clearComparisonMapForUser(any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getSongDetailsForFriendsPage_success() throws Exception {
        FriendsService service = mock();
        FriendsController controller = new FriendsController(service);

        SongDetailsFriendsRequest request = new SongDetailsFriendsRequest("Love Story");
        Song mockSong = new Song("Love Story", "Taylor Swift", "2008", "We were both young...");
        when(service.getSongDetailsFromFriendsPage("Love Story")).thenReturn(mockSong);

        ResponseEntity<Song> response = controller.getSongDetailsForFriendsPage(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Love Story", response.getBody().getTitle());
    }
}
