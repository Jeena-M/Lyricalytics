package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SongDetailsFriendsRequestTest {

    @Test
    void noArgConstructor_setsNullSongName() {
        SongDetailsFriendsRequest request = new SongDetailsFriendsRequest();
        // Since there's no setter, songName should be null by default
        assertNull(request.getSongName());
    }

    @Test
    void parameterizedConstructor_setsSongNameCorrectly() {
        SongDetailsFriendsRequest request = new SongDetailsFriendsRequest("Love Story");
        assertEquals("Love Story", request.getSongName());
    }
}
