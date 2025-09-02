package edu.usc.csci310.project.controllers;

import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.models.WordFrequency;
import edu.usc.csci310.project.requests.*;
import edu.usc.csci310.project.services.FavoritesService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FavoritesControllerTest {
    @Test
    public void generateWordCloudFromFavoritesSuccess() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        FavoritesListSongs request = new FavoritesListSongs("zoe");

        List<WordFrequency> expectedWordFrequencies = new ArrayList<>();
        expectedWordFrequencies.add(new WordFrequency("word", 1));
        when(favoritesService.generateWordCloudFromFavoritesList(request.getUsername())).thenReturn(expectedWordFrequencies);

        ResponseEntity<List<WordFrequency>> output = controller.generateWordCloudFromFavorites(request);
        assertEquals("word", output.getBody().get(0).getWord());
    }

    @Test
    public void generateWordCloudFromFavoritesNullUsername() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        FavoritesListSongs request = new FavoritesListSongs();

        ResponseEntity<List<WordFrequency>> output = controller.generateWordCloudFromFavorites(request);
        assertEquals(HttpStatus.BAD_REQUEST, output.getStatusCode());
    }

    @Test
    public void generateWordCloudFromFavoritesException() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        FavoritesListSongs request = new FavoritesListSongs("zoe");

        when(favoritesService.generateWordCloudFromFavoritesList(request.getUsername())).thenThrow(new RuntimeException("error"));

        ResponseEntity<List<WordFrequency>> output = controller.generateWordCloudFromFavorites(request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, output.getStatusCode());
    }

    @Test
    public void getFavoritesSongsSuccess() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        FavoritesListSongs request = new FavoritesListSongs("zoe");

        List<Song> songs = new ArrayList<>();
        songs.add(new Song("Happier Than Ever", "Billie Eilish", "2021", "lots of lyrics"));

        when(favoritesService.getFavoritesForUser("zoe")).thenReturn(songs);
        assertEquals("Happier Than Ever", controller.getFavoritesSongs(request).getBody().get(0).getTitle());
    }

    @Test
    public void getFavoritesSongsException() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        FavoritesListSongs request = new FavoritesListSongs("zoe");

        when(favoritesService.getFavoritesForUser("zoe")).thenThrow(new RuntimeException("error"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.getFavoritesSongs(request).getStatusCode());
    }

    @Test
    public void addSongToFavoritesSuccess() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        AddSongToFavorites request = new AddSongToFavorites("Burnout Days", "flipturn", "zoe");

        when(favoritesService.addSongToFavoritesList(request)).thenReturn(1);

        assertEquals("Song added with ID: 1", controller.addSongToFavorites(request).getBody());
    }

    @Test
    public void addSongToFavoritesException() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        AddSongToFavorites request = new AddSongToFavorites("Burnout Days", "flipturn", "zoe");

        when(favoritesService.addSongToFavoritesList(request)).thenThrow(new RuntimeException("error"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.addSongToFavorites(request).getStatusCode());
    }

    @Test
    public void addSongToFavoritesDuplicate() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        AddSongToFavorites request = new AddSongToFavorites("Burnout Days", "flipturn", "zoe");

        when(favoritesService.addSongToFavoritesList(request)).thenReturn(0);

        assertEquals("Song not added - duplicate song", controller.addSongToFavorites(request).getBody());
    }

    @Test
    public void deleteAllSongsSuccess() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        FavoritesListSongs request = new FavoritesListSongs("zoe");

        when(favoritesService.deleteFavoritesByUsername(request.getUsername())).thenReturn(2);

        assertEquals("Deleted 2 favorite(s) for user: zoe", controller.deleteAllSongsForUser(request).getBody());
    }

    @Test
    public void deleteAllSongsException() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        FavoritesListSongs request = new FavoritesListSongs("zoe");

        when(favoritesService.deleteFavoritesByUsername(request.getUsername())).thenThrow(new RuntimeException("error"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.deleteAllSongsForUser(request).getStatusCode());
    }

    @Test
    public void deleteSongFromFavoritesSuccess() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        DeleteSongFromFavorites request = new DeleteSongFromFavorites("Burnout Days", "flipturn", "zoe");

        when(favoritesService.deleteSongByUsernameAndSong(request.getUsername(), request.getSongName(), request.getArtistName())).thenReturn(1);

        assertEquals("Deleted 1 favorite for user: zoe", controller.deleteSongFromFavorites(request).getBody());
    }

    @Test
    public void deleteSongFromFavoritesException() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        DeleteSongFromFavorites request = new DeleteSongFromFavorites("Burnout Days", "flipturn", "zoe");

        when(favoritesService.deleteSongByUsernameAndSong(request.getUsername(), request.getSongName(), request.getArtistName())).thenThrow(new RuntimeException("error"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.deleteSongFromFavorites(request).getStatusCode());
    }

    @Test
    public void moveSongUpSuccess() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        ReOrderSongRequest request = new ReOrderSongRequest("Burnout Days", "flipturn", "zoe");

        assertEquals("Reordered song", controller.moveSongUp(request).getBody());
    }

    @Test
    public void moveSongUpException() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        ReOrderSongRequest request = new ReOrderSongRequest("Burnout Days", "flipturn", "zoe");

        doThrow(new RuntimeException("error")).when(favoritesService).moveSongUpInFavoritesList(request.getUsername(), request.getSongName(), request.getArtistName());

        assertEquals("Failed to reorder song", controller.moveSongUp(request).getBody());
    }

    @Test
    public void moveSongDownSuccess() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        ReOrderSongRequest request = new ReOrderSongRequest("Burnout Days", "flipturn", "zoe");

        assertEquals("Reordered song", controller.moveSongDown(request).getBody());
    }

    @Test
    public void moveSongDownException() throws Exception {
        FavoritesService favoritesService = mock();
        FavoritesController controller = new FavoritesController(favoritesService);
        ReOrderSongRequest request = new ReOrderSongRequest("Burnout Days", "flipturn", "zoe");

        doThrow(new RuntimeException("error")).when(favoritesService).moveSongDownInFavoritesList(request.getUsername(), request.getSongName(), request.getArtistName());

        assertEquals("Failed to reorder song", controller.moveSongDown(request).getBody());
    }

    @Test
    public void updateExistingWordcloudSuccess() throws Exception {
        FavoritesService favoritesService = mock();
        UpdateExistingWordCloudRequest request = new UpdateExistingWordCloudRequest("zoe", "words");
        List<WordFrequency> wf = new ArrayList<>();
        WordFrequency w = new WordFrequency("words", 1);
        wf.add(w);
        when(favoritesService.updateExistingWordCloud(request.getUsername(), request.getWordCloud())).thenReturn(wf);
        FavoritesController controller = new FavoritesController(favoritesService);
        assertEquals("words", controller.updateExistingWordCloudWithFavorites(request).getBody().get(0).getWord());
    }

    @Test
    public void updateExistingWordcloudException() throws Exception {
        FavoritesService favoritesService = mock();
        UpdateExistingWordCloudRequest request = new UpdateExistingWordCloudRequest("zoe", "words");
        List<WordFrequency> wf = new ArrayList<>();
        when(favoritesService.updateExistingWordCloud(request.getUsername(), request.getWordCloud())).thenThrow(new RuntimeException("error"));
        FavoritesController controller = new FavoritesController(favoritesService);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.updateExistingWordCloudWithFavorites(request).getStatusCode());
    }

    @Test
    public void togglePrivacySuccess() throws Exception {
        FavoritesService favoritesService = mock();
        PrivacyRequest request = new PrivacyRequest("zoe", false);
        when(favoritesService.togglePrivacyMode(request.getPrivacy(), request.getUsername())).thenReturn("Privacy setting updated.");
        FavoritesController controller = new FavoritesController(favoritesService);
        assertEquals("Privacy setting updated.", controller.togglePrivacy(request).getBody());
    }

    @Test
    public void togglePrivacyException() throws Exception {
        FavoritesService favoritesService = mock();
        PrivacyRequest request = new PrivacyRequest("zoe", false);
        when(favoritesService.togglePrivacyMode(request.getPrivacy(), request.getUsername())).thenThrow(new RuntimeException("error"));
        FavoritesController controller = new FavoritesController(favoritesService);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.togglePrivacy(request).getStatusCode());
    }

    @Test
    public void getPrivacyException() throws Exception {
        FavoritesService favoritesService = mock();
        when(favoritesService.getPrivacyMode("user1")).thenThrow(new RuntimeException("error"));
        FavoritesController controller = new FavoritesController(favoritesService);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.getPrivacy("user1").getStatusCode());
    }

    @Test
    void getPrivacy_success() throws SQLException {
        FavoritesService service = mock();
        FavoritesController controller = new FavoritesController(service);

        when(service.getPrivacyMode("jeena")).thenReturn(true);

        ResponseEntity<Map<String, Boolean>> response = controller.getPrivacy("jeena");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("isPrivate"));
    }


}
