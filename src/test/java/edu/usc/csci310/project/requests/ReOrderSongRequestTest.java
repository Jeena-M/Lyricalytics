package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReOrderSongRequestTest {
    @Test
    public void getSongNameTest(){
        ReOrderSongRequest re = new ReOrderSongRequest("Maine", "Noah Kahan", "zoe");
        assertEquals("Maine", re.getSongName());
    }

    @Test
    public void setSongNameTest(){
        ReOrderSongRequest re = new ReOrderSongRequest("Maine", "Noah Kahan", "zoe");
        re.setSongName("Northern Attitude");
        assertEquals("Northern Attitude", re.getSongName());
    }

    @Test
    public void getArtistNameTest(){
        ReOrderSongRequest re = new ReOrderSongRequest("Maine", "Noah Kahan", "zoe");
        assertEquals("Noah Kahan", re.getArtistName());
    }

    @Test
    public void setArtistNameTest(){
        ReOrderSongRequest re = new ReOrderSongRequest("Maine", "Noah Kahan", "zoe");
        re.setArtistName("Kahan");
        assertEquals("Kahan", re.getArtistName());
    }

    @Test
    public void getUsernameTest(){
        ReOrderSongRequest re = new ReOrderSongRequest("Maine", "Noah Kahan", "zoe");
        assertEquals("zoe", re.getUsername());
    }

    @Test
    public void setUsernameTest(){
        ReOrderSongRequest re = new ReOrderSongRequest("Maine", "Noah Kahan", "zoe");
        re.setUsername("zoe2");
        assertEquals("zoe2", re.getUsername());
    }
}
