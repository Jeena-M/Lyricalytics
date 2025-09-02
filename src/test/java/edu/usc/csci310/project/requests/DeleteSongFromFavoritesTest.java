package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteSongFromFavoritesTest {
    @Test
    public void getSongNameTest(){
        DeleteSongFromFavorites del = new DeleteSongFromFavorites("Maine", "Noah Kahan", "zoe");
        assertEquals("Maine", del.getSongName());
    }

    @Test
    public void setSongNameTest(){
        DeleteSongFromFavorites del = new DeleteSongFromFavorites("Maine", "Noah Kahan", "zoe");
        del.setSongName("Northern Attitude");
        assertEquals("Northern Attitude", del.getSongName());
    }

    @Test
    public void getArtistNameTest(){
        DeleteSongFromFavorites del = new DeleteSongFromFavorites("Maine", "Noah Kahan", "zoe");
        assertEquals("Noah Kahan", del.getArtistName());
    }

    @Test
    public void setArtistNameTest(){
        DeleteSongFromFavorites del = new DeleteSongFromFavorites("Maine", "Noah Kahan", "zoe");
        del.setArtistName("Kahan");
        assertEquals("Kahan", del.getArtistName());
    }

    @Test
    public void getUsernameTest(){
        DeleteSongFromFavorites del = new DeleteSongFromFavorites("Maine", "Noah Kahan", "zoe");
        assertEquals("zoe", del.getUsername());
    }

    @Test
    public void setUsernameTest(){
        DeleteSongFromFavorites del = new DeleteSongFromFavorites("Maine", "Noah Kahan", "zoe");
        del.setUsername("zoe2");
        assertEquals("zoe2", del.getUsername());
    }
}
