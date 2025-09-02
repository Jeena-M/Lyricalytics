package edu.usc.csci310.project.controllers;

import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.requests.SoulmateEnemyRequest;
import edu.usc.csci310.project.services.SoulmateEnemyService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SoulmateEnemyControllerTest {
    @Test
    public void getSoulmateForUserTest() throws Exception {
        SoulmateEnemyService se = mock();
        SoulmateEnemyRequest req = new SoulmateEnemyRequest("user1");
        when(se.getSoulmateForUser(req.getUsername())).thenReturn("user2");
        List<Song> soulmateSongs = new ArrayList<>();
        Song s = new Song("Blue", "Billie Eilish", "2024", "lyrics");
        soulmateSongs.add(s);
        when(se.getFavoriteSongs("user2")).thenReturn(soulmateSongs);
        SoulmateEnemyController controller = new SoulmateEnemyController(se);
        assertEquals("user2", controller.getSoulmateForUser(req).getBody().getUsername());
    }

    @Test
    public void getSoulmateForUserNoUsernameTest() throws Exception {
        SoulmateEnemyService se = mock();
        SoulmateEnemyRequest req = new SoulmateEnemyRequest();
        SoulmateEnemyController controller = new SoulmateEnemyController(se);
        assertEquals(HttpStatus.BAD_REQUEST, controller.getSoulmateForUser(req).getStatusCode());
    }

    @Test
    public void getSoulmateForUserErrorTest() throws Exception {
        SoulmateEnemyService se = mock();
        SoulmateEnemyRequest req = new SoulmateEnemyRequest("user1");
        when(se.getSoulmateForUser(req.getUsername())).thenThrow(new RuntimeException("error"));
        List<Song> soulmateSongs = new ArrayList<>();
        when(se.getFavoriteSongs("user2")).thenReturn(soulmateSongs);
        SoulmateEnemyController controller = new SoulmateEnemyController(se);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.getSoulmateForUser(req).getStatusCode());
    }

    @Test
    public void getEnemyForUserTest() throws Exception {
        SoulmateEnemyService se = mock();
        SoulmateEnemyRequest req = new SoulmateEnemyRequest("user1");
        when(se.getEnemyForUser(req.getUsername())).thenReturn("user2");
        List<Song> soulmateSongs = new ArrayList<>();
        Song s = new Song("Blue", "Billie Eilish", "2024", "lyrics");
        soulmateSongs.add(s);
        when(se.getFavoriteSongs("user2")).thenReturn(soulmateSongs);
        SoulmateEnemyController controller = new SoulmateEnemyController(se);
        assertEquals("user2", controller.getEnemyForUser(req).getBody().getUsername());
    }

    @Test
    public void getEnemyForUserNoUsernameTest() throws Exception {
        SoulmateEnemyService se = mock();
        SoulmateEnemyRequest req = new SoulmateEnemyRequest();
        SoulmateEnemyController controller = new SoulmateEnemyController(se);
        assertEquals(HttpStatus.BAD_REQUEST, controller.getEnemyForUser(req).getStatusCode());
    }

    @Test
    public void getEnemyForUserErrorTest() throws Exception {
        SoulmateEnemyService se = mock();
        SoulmateEnemyRequest req = new SoulmateEnemyRequest("user1");
        when(se.getEnemyForUser(req.getUsername())).thenThrow(new RuntimeException("error"));
        List<Song> soulmateSongs = new ArrayList<>();
        when(se.getFavoriteSongs("user2")).thenReturn(soulmateSongs);
        SoulmateEnemyController controller = new SoulmateEnemyController(se);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.getEnemyForUser(req).getStatusCode());
    }
}
