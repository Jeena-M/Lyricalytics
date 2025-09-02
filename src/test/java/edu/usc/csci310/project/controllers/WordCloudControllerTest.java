package edu.usc.csci310.project.controllers;

import edu.usc.csci310.project.models.Artist;
import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.models.SongFrequency;
import edu.usc.csci310.project.models.WordFrequency;
import edu.usc.csci310.project.requests.*;
import edu.usc.csci310.project.services.WordCloudService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WordCloudControllerTest {

    @Test
    void generateWordCloudTest1() throws Exception {
        WordCloudService service = mock();
        List<WordFrequency> expectedWordFrequencies = new ArrayList<>();
        expectedWordFrequencies.add(new WordFrequency("word", 1));
        WordCloudGenerateRequest request = new WordCloudGenerateRequest("artist", 7, "jeena");

        when(service.generateWordCloud("artist", 7, "jeena")).thenReturn(expectedWordFrequencies);

        WordCloudController controller = new WordCloudController(service);
        ResponseEntity<List<WordFrequency>> output = controller.generateWordCloud(request);
        assertEquals("word", output.getBody().get(0).getWord());
    }

    @Test
    void generateWordCloudTest2() throws Exception {
        WordCloudService service = mock();

        WordCloudGenerateRequest request = new WordCloudGenerateRequest("", 7, "jeena");

        WordCloudController controller = new WordCloudController(service);
        assertEquals( HttpStatus.BAD_REQUEST, controller.generateWordCloud(request).getStatusCode());

    }

    @Test
    void generateWordCloudTest3() throws Exception {
        WordCloudService service = mock();

        WordCloudGenerateRequest request = new WordCloudGenerateRequest(null, 7, "jeena");

        WordCloudController controller = new WordCloudController(service);
        assertEquals( HttpStatus.BAD_REQUEST, controller.generateWordCloud(request).getStatusCode());

    }

    @Test
    void generateWordCloudTest4() throws Exception {
        WordCloudService service = mock();

        WordCloudGenerateRequest request = new WordCloudGenerateRequest("artist", 7, "jeena");
        when(service.generateWordCloud("artist", 7, "jeena")).thenThrow(new Exception("Can't generate wordcloud"));

        WordCloudController controller = new WordCloudController(service);
        assertEquals(controller.generateWordCloud(request).getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void generateWordCloudNoNumberTest1() {
        WordCloudService service = mock();

        WordCloudNoNumberGenerateRequest request = new WordCloudNoNumberGenerateRequest("");

        WordCloudController controller = new WordCloudController(service);
        assertEquals( HttpStatus.BAD_REQUEST, controller.generateWordCloudNoNumber(request).getStatusCode());

    }

    @Test
    void generateWordCloudNoNumberTest2() {
        WordCloudService service = mock();

        WordCloudNoNumberGenerateRequest request = new WordCloudNoNumberGenerateRequest(null);

        WordCloudController controller = new WordCloudController(service);
        assertEquals( HttpStatus.BAD_REQUEST, controller.generateWordCloudNoNumber(request).getStatusCode());

    }

//    @Test
//    void generateWordCloudNoNumberTest3() throws Exception {
//        WordCloudService service = mock();
//
//        WordCloudNoNumberGenerateRequest request = new WordCloudNoNumberGenerateRequest("artist");
//        when(service.generateWordCloudNoNumber("artist", "jeena")).thenThrow(new Exception("Can't generate word cloud"));
//
//        WordCloudController controller = new WordCloudController(service);
//        assertEquals(controller.generateWordCloudNoNumber(request).getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
//
//    }


    @Test
    void generateWordCloudNoNumberTest4() throws Exception {
        WordCloudService service = mock();
        List<WordFrequency> expectedWordFrequencies = new ArrayList<>();
        expectedWordFrequencies.add(new WordFrequency("word", 1));
        WordCloudNoNumberGenerateRequest request = new WordCloudNoNumberGenerateRequest();
        request.setArtistName("artist");
        request.setUsername("user1");
        when(service.generateWordCloudNoNumber("artist", "user1")).thenReturn(expectedWordFrequencies);

        WordCloudController controller = new WordCloudController(service);
        ResponseEntity<List<WordFrequency>> output = controller.generateWordCloudNoNumber(request);
        assertEquals("word", output.getBody().get(0).getWord());
    }


    @Test
    void getSongFrequencyForWordInLyricsTest1() throws Exception {
        WordCloudService service = mock();
        List<SongFrequency> expectedWordFrequencies = new ArrayList<>();
        expectedWordFrequencies.add(new SongFrequency("song", 1));
        SongFrequencyRequest request = new SongFrequencyRequest("artist", 1, "word", "jeena");
        when(service.getSongFrequencyInLyrics("word", "artist", 1, "jeena")).thenReturn(expectedWordFrequencies);

        WordCloudController controller = new WordCloudController(service);
        ResponseEntity<List<SongFrequency>> output = controller.getSongFrequencyForWordInLyrics(request);
        assertEquals("song", output.getBody().get(0).getSong());
    }

    @Test
    void getSongFrequencyForWordInLyricsTest2() throws Exception {
        WordCloudService service = mock();

        SongFrequencyRequest request = new SongFrequencyRequest("artist", 1, "word", "jeena");
        when(service.getSongFrequencyInLyrics("word", "artist", 1, "jeena")).thenThrow(new Exception("Can't generate frequencies"));

        WordCloudController controller = new WordCloudController(service);

        assertEquals(controller.getSongFrequencyForWordInLyrics(request).getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void getSongFrequencyForWordInLyricsNoNumberTest1() throws Exception {
        WordCloudService service = mock();
        List<SongFrequency> expectedWordFrequencies = new ArrayList<>();
        expectedWordFrequencies.add(new SongFrequency("song", 1));
        SongFrequencyRequest request = new SongFrequencyRequest("artist", 1, "word", "jeena");
        when(service.getSongFrequencyInLyricsNoCount("word", "artist", 1, "jeena")).thenReturn(expectedWordFrequencies);

        WordCloudController controller = new WordCloudController(service);
        ResponseEntity<List<SongFrequency>> output = controller.getSongFrequencyForWordInLyricsNoNumber(request);
        assertEquals("song", output.getBody().get(0).getSong());
    }

    @Test
    void getSongFrequencyForWordInLyricsNoNumberTest2() throws Exception {
        WordCloudService service = mock();

        SongFrequencyRequest request = new SongFrequencyRequest("artist", 1, "word", "jeena");
        when(service.getSongFrequencyInLyricsNoCount("word", "artist", 1, "jeena")).thenThrow(new Exception("Can't generate frequencies"));

        WordCloudController controller = new WordCloudController(service);

        assertEquals(controller.getSongFrequencyForWordInLyricsNoNumber(request).getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void getSongDetailsTest() throws Exception {
        WordCloudService service = mock();
        Song expectedSong = new Song("title", "artist", "year", "lyrics");
        when(service.getSongDetails("title", "artist", "jeena")).thenReturn(expectedSong);
        WordCloudController controller = new WordCloudController(service);
        SongDetailRequest request = new SongDetailRequest("artist", "title", "jeena");
        ResponseEntity<Song> output = controller.getSongDetails(request);
        assertEquals("title", output.getBody().getTitle());
    }

    @Test
    void getSongDetailsFavoritesTest() throws Exception {
        WordCloudService service = mock();
        Song expectedSong = new Song("title", "artist", "year", "lyrics");
        when(service.getSongDetailsFavorites("title", "artist")).thenReturn(expectedSong);
        WordCloudController controller = new WordCloudController(service);
        SongDetailsFavorites request = new SongDetailsFavorites("artist", "title");
        ResponseEntity<Song> output = controller.getSongDetailsFavorites(request);
        assertEquals("title", output.getBody().getTitle());
    }

    @Test
    void generateWordCloudFromListTestArtistEmpty() {
        WordCloudService service = mock();

        List<String> songs = new ArrayList<>();
        WordCloudFromListGenerateRequest request = new WordCloudFromListGenerateRequest("", songs, "jeena");

        WordCloudController controller = new WordCloudController(service);
        assertEquals( HttpStatus.BAD_REQUEST, controller.generateWordCloudFromList(request).getStatusCode());
    }

    @Test
    void generateWordCloudFromListTestArtistNull() {
        WordCloudService service = mock();

        List<String> songs = new ArrayList<>();
        String artist = null;
        WordCloudFromListGenerateRequest request = new WordCloudFromListGenerateRequest(artist, songs, "jeena");

        WordCloudController controller = new WordCloudController(service);
        assertEquals( HttpStatus.BAD_REQUEST, controller.generateWordCloudFromList(request).getStatusCode());
    }

    @Test
    void generateWordCloudFromListTestSongsEmpty() {
        WordCloudService service = mock();

        List<String> songs = new ArrayList<>();
        WordCloudFromListGenerateRequest request = new WordCloudFromListGenerateRequest("artist", songs, "jeena");

        WordCloudController controller = new WordCloudController(service);
        assertEquals( HttpStatus.BAD_REQUEST, controller.generateWordCloudFromList(request).getStatusCode());
    }

    @Test
    void generateWordCloudFromListTestSongsNull() {
        WordCloudService service = mock();

        List<String> songs = null;
        WordCloudFromListGenerateRequest request = new WordCloudFromListGenerateRequest("artist", songs, "jeena");

        WordCloudController controller = new WordCloudController(service);
        assertEquals( HttpStatus.BAD_REQUEST, controller.generateWordCloudFromList(request).getStatusCode());
    }

    @Test
    void generateWordCloudFromListSuccess() throws Exception {
        WordCloudService service = mock();
        List<WordFrequency> expectedWordFrequencies = new ArrayList<>();
        expectedWordFrequencies.add(new WordFrequency("word", 1));
        List<String> songs = new ArrayList<>();
        songs.add("song");
        WordCloudFromListGenerateRequest request = new WordCloudFromListGenerateRequest("artist", songs, "jeena");
        when(service.generateWordCloudFromSongList("artist", songs, "jeena")).thenReturn(expectedWordFrequencies);

        WordCloudController controller = new WordCloudController(service);
        ResponseEntity<List<WordFrequency>> output = controller.generateWordCloudFromList(request);
        assertEquals("word", output.getBody().get(0).getWord());
    }

    @Test
    void generateWordCloudFromListException() throws Exception {
        WordCloudService service = mock();

        List<String> songs = new ArrayList<>();
        songs.add("song");
        WordCloudFromListGenerateRequest request = new WordCloudFromListGenerateRequest("artist", songs, "jeena");
        when(service.generateWordCloudFromSongList("artist", songs, "jeena")).thenThrow(new Exception("Can't generate word cloud"));

        WordCloudController controller = new WordCloudController(service);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.generateWordCloudFromList(request).getStatusCode());

    }

    @Test
    void getAmbiguousArtistsTest() throws Exception {
        WordCloudService service = mock();
        ArtistsRequest artistsRequest = new ArtistsRequest("Justin");
        List<Artist> artists = new ArrayList<>();
        artists.add(new Artist("Justin Bieber", "fakeurl"));
        artists.add(new Artist("Justin Timberlake", "fakeurl"));
        when(service.getAmbiguousArtists("Justin")).thenReturn(artists);
        WordCloudController controller = new WordCloudController(service);
        assertEquals(controller.getAmbiguousArtists(artistsRequest).getBody().size(), 2);
        assertEquals(controller.getAmbiguousArtists(artistsRequest).getBody().get(0).getName(), "Justin Bieber");
    }

    @Test
    void getSongsForArtistTest() throws Exception {
        WordCloudService service = mock();
        ArtistsRequest artistsRequest = new ArtistsRequest("Lizzy McAlpine");
        List<String> songs = new ArrayList<>();
        songs.add("Older");
        songs.add("ceilings");
        when(service.getSongsForArtist("Lizzy McAlpine")).thenReturn(songs);
        WordCloudController controller = new WordCloudController(service);
        assertEquals(controller.getSongsForArtist(artistsRequest).getBody().get(0), "Older");
    }

    @Test
    void generateNoNumberTest4() throws Exception {
        WordCloudNoNumberGenerateRequest request = new WordCloudNoNumberGenerateRequest();
        WordCloudService wordCloudService = mock();
        WordCloudController controller = new WordCloudController(wordCloudService);
        request.setArtistName("Taylor Swift");
        request.setUsername("user1");

        when(wordCloudService.generateWordCloudNoNumber("Taylor Swift", "user1"))
                .thenThrow(new RuntimeException("Something went wrong"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.generateWordCloudNoNumber(request).getStatusCode());
    }

    @Test
    void updateWordCloudTest1() throws Exception {
        List<String> songs = new ArrayList<>();
        UpdateWordCloudFromSongList request = new UpdateWordCloudFromSongList("artist", songs, "jeena", "wordcloud");
        WordCloudService wordCloudService = mock();
        WordCloudController controller = new WordCloudController(wordCloudService);
        List<WordFrequency> expectedWordFrequencies = new ArrayList<>();
        expectedWordFrequencies.add(new WordFrequency("word", 1));

        when(wordCloudService.updateWordCloudFromSongList("artist", songs, "jeena", "wordcloud")).thenReturn(expectedWordFrequencies);
        assertEquals("word", controller.updateExistingWordCloudWithList(request).getBody().get(0).getWord());
    }

    @Test
    void updateWordCloudTest2() throws Exception {
        List<String> songs = new ArrayList<>();
        UpdateWordCloudFromSongList request = new UpdateWordCloudFromSongList("artist", songs, "jeena", "wordcloud");

        WordCloudService wordCloudService = mock(WordCloudService.class);

        WordCloudController controller = new WordCloudController(wordCloudService);

        when(wordCloudService.updateWordCloudFromSongList("artist", songs, "jeena", "wordcloud"))
                .thenThrow(new RuntimeException("Something went wrong"));

        ResponseEntity<List<WordFrequency>> response = controller.updateExistingWordCloudWithList(request);

       assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error updating word count data based on favorites: Something went wrong",
                response.getHeaders().getFirst("X-Error-Message"));
    }

    @Test
    void updateExistingWordCloudWithSearchSongCount_success() throws Exception {
        WordCloudService wordCloudService = mock();
        WordCloudController controller = new WordCloudController(wordCloudService);

        UpdateExistingWordCloudWithSearchWithSongCount request =
                new UpdateExistingWordCloudWithSearchWithSongCount("Taylor Swift", 2, "user1", "[]");

        List<WordFrequency> expectedWordFrequencies = new ArrayList<>();
        expectedWordFrequencies.add(new WordFrequency("love", 3));

        when(wordCloudService.updateWordCloudFromSongListWithSongCount("Taylor Swift", 2, "user1", "[]"))
                .thenReturn(expectedWordFrequencies);

        ResponseEntity<List<WordFrequency>> response = controller.updateExistingWordCloudSearchSongCount(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("love", response.getBody().get(0).getWord());
        assertEquals(3, response.getBody().get(0).getCount());
    }

    @Test
    void updateExistingWordCloudWithSearchSongCount_exception() throws Exception {
        WordCloudService wordCloudService = mock();
        WordCloudController controller = new WordCloudController(wordCloudService);

        UpdateExistingWordCloudWithSearchWithSongCount request =
                new UpdateExistingWordCloudWithSearchWithSongCount("Taylor Swift", 2, "user1", "[]");

        when(wordCloudService.updateWordCloudFromSongListWithSongCount("Taylor Swift", 2, "user1", "[]"))
                .thenThrow(new RuntimeException("Something went wrong"));

        ResponseEntity<List<WordFrequency>> response = controller.updateExistingWordCloudSearchSongCount(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error updating word count data based on favorites: Something went wrong",
                response.getHeaders().getFirst("X-Error-Message"));
    }

}