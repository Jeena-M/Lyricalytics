package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FavoritesListSongsTest {
    @Test
    public void getUsernameTest() {
        FavoritesListSongs fls = new FavoritesListSongs("zoe");
        assertEquals("zoe", fls.getUsername());
    }

    @Test
    public void setUsernameTest() {
        FavoritesListSongs fls = new FavoritesListSongs("zoe");
        fls.setUsername("zoe2");
        assertEquals("zoe2", fls.getUsername());
    }
}
