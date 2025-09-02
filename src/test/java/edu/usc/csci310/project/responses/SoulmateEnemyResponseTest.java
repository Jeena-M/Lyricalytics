package edu.usc.csci310.project.responses;

import edu.usc.csci310.project.models.Song;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SoulmateEnemyResponseTest {
    @Test
    public void setUserNameTest() {
        List<Song> songs = new ArrayList<>();
        SoulmateEnemyResponse res = new SoulmateEnemyResponse("user1", songs);
        res.setUsername("user2");
        assertEquals("user2", res.getUsername());
    }

    @Test
    public void setFavoritesTest() {
        List<Song> songs = new ArrayList<>();
        List<Song> newSongs = new ArrayList<>();
        SoulmateEnemyResponse res = new SoulmateEnemyResponse("user1", songs);
        res.setFavorites(newSongs);
        assertEquals(newSongs, res.getFavorites());
    }
}
