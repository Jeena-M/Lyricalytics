package edu.usc.csci310.project.services;

import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.models.SongFrequency;
import edu.usc.csci310.project.models.WordFrequency;
import edu.usc.csci310.project.requests.LyricsRequest;
import edu.usc.csci310.project.requests.SpotifyRequest;
import edu.usc.csci310.project.requests.WordFrequencyRequest;
import edu.usc.csci310.project.responses.Image;
import edu.usc.csci310.project.responses.Item;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WordCloudServiceTest {

    private List<String> buildProcessedLyrics() {
        List<String> processedLyrics = new ArrayList<>();
        processedLyrics.add("Yes");
        return processedLyrics;
    }

    private List<WordFrequency> buildExpectedWordFrequencies() {
        List<WordFrequency> expected = new ArrayList<>();
        expected.add(new WordFrequency("Yes", 1));
        return expected;
    }

    private void setupLyricsRequest(MockedStatic<LyricsRequest> mockedLyrics, String song, String artist, String lyrics) throws Exception {
        if (lyrics == null) {
            mockedLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist(song, artist))
                    .thenThrow(new Exception("Something went wrong"));
        } else {
            mockedLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist(song, artist))
                    .thenReturn(lyrics);
        }
    }

    private void setupCommonMocks(MockedStatic<LyricsProcessing> mockedProcessing,
                                  MockedStatic<WordFrequencyRequest> mockedWordFreq,
                                  String lyricsForAllSongs,
                                  List<String> processedLyrics) {
        mockedProcessing.when(() -> LyricsProcessing.processLyrics(lyricsForAllSongs))
                .thenReturn(processedLyrics);

        mockedWordFreq.when(() -> WordFrequencyRequest.getTop100WordFrequencies(processedLyrics))
                .thenReturn(buildExpectedWordFrequencies());
    }

    @Test
    void generateWordCloudTest() throws Exception {
        try (MockedStatic<LyricsRequest> mockedLyrics = mockStatic(LyricsRequest.class);
             MockedStatic<LyricsProcessing> mockedProcessing = mockStatic(LyricsProcessing.class);
             MockedStatic<WordFrequencyRequest> mockedWordFreq = mockStatic(WordFrequencyRequest.class)) {

            SpotifyRequest spotReq = mock(SpotifyRequest.class);
            List<String> songs = new ArrayList<>();
            songs.add("song");
            when(spotReq.getNumPopularSongs("artist", 1)).thenReturn(songs);

            setupLyricsRequest(mockedLyrics, "song", "artist", "Yes");

            String lyricsForAllSongs = "Yes\n";
            List<String> processedLyrics = buildProcessedLyrics();

            setupCommonMocks(mockedProcessing, mockedWordFreq, lyricsForAllSongs, processedLyrics);

            WordCloudService service = new WordCloudService(spotReq);
            List<WordFrequency> output = service.generateWordCloud("artist", 1, "jeena");

            assertEquals(1, output.size());
            assertEquals("Yes", output.get(0).getWord());
            assertEquals(1, output.get(0).getCount());
        }
    }

    @Test
    void generateWordCloudTest2() throws Exception {
        try (MockedStatic<LyricsRequest> mockedLyrics = mockStatic(LyricsRequest.class);
             MockedStatic<LyricsProcessing> mockedProcessing = mockStatic(LyricsProcessing.class);
             MockedStatic<WordFrequencyRequest> mockedWordFreq = mockStatic(WordFrequencyRequest.class)) {

            SpotifyRequest spotReq = mock(SpotifyRequest.class);
            List<String> songs = new ArrayList<>();
            songs.add("song");
            songs.add("song2");
            when(spotReq.getNumPopularSongs("artist", 1)).thenReturn(songs);

            setupLyricsRequest(mockedLyrics, "song", "artist", "Yes");
            setupLyricsRequest(mockedLyrics, "song2", "artist", null);

            String lyricsForAllSongs = "Yes\n";
            List<String> processedLyrics = buildProcessedLyrics();

            setupCommonMocks(mockedProcessing, mockedWordFreq, lyricsForAllSongs, processedLyrics);

            WordCloudService service = new WordCloudService(spotReq);
            List<WordFrequency> output = service.generateWordCloud("artist", 1, "jeena");

            assertEquals(1, output.size());
            assertEquals("Yes", output.get(0).getWord());
            assertEquals(1, output.get(0).getCount());
        }
    }

    @Test
    void generateWordCloudNoNumberTest1() throws Exception {
        try (MockedStatic<LyricsRequest> mockedLyrics = mockStatic(LyricsRequest.class);
             MockedStatic<LyricsProcessing> mockedProcessing = mockStatic(LyricsProcessing.class);
             MockedStatic<WordFrequencyRequest> mockedWordFreq = mockStatic(WordFrequencyRequest.class)) {

            SpotifyRequest spotReq = mock(SpotifyRequest.class);
            List<String> songs = new ArrayList<>();
            songs.add("song");
            when(spotReq.getAllSongsForArtist("artist")).thenReturn(songs);

            setupLyricsRequest(mockedLyrics, "song", "artist", "Yes");

            String lyricsForAllSongs = "Yes\n";
            List<String> processedLyrics = buildProcessedLyrics();

            setupCommonMocks(mockedProcessing, mockedWordFreq, lyricsForAllSongs, processedLyrics);

            WordCloudService service = new WordCloudService(spotReq);
            List<WordFrequency> output = service.generateWordCloudNoNumber("artist", "jeena");

            assertEquals(1, output.size());
            assertEquals("Yes", output.get(0).getWord());
            assertEquals(1, output.get(0).getCount());
        }
    }

    @Test
    void generateWordCloudNoNumberTest2() throws Exception {
        try (MockedStatic<LyricsRequest> mockedLyrics = mockStatic(LyricsRequest.class);
             MockedStatic<LyricsProcessing> mockedProcessing = mockStatic(LyricsProcessing.class);
             MockedStatic<WordFrequencyRequest> mockedWordFreq = mockStatic(WordFrequencyRequest.class)) {

            SpotifyRequest spotReq = mock(SpotifyRequest.class);
            List<String> songs = new ArrayList<>();
            songs.add("song");
            songs.add("song2");
            when(spotReq.getAllSongsForArtist("artist")).thenReturn(songs);

            setupLyricsRequest(mockedLyrics, "song", "artist", "Yes");
            setupLyricsRequest(mockedLyrics, "song2", "artist", null);

            String lyricsForAllSongs = "Yes\n";
            List<String> processedLyrics = buildProcessedLyrics();

            setupCommonMocks(mockedProcessing, mockedWordFreq, lyricsForAllSongs, processedLyrics);

            WordCloudService service = new WordCloudService(spotReq);
            List<WordFrequency> output = service.generateWordCloudNoNumber("artist", "jeena");

            assertEquals(1, output.size());
            assertEquals("Yes", output.get(0).getWord());
            assertEquals(1, output.get(0).getCount());
        }
    }

    @Test
    void getSongFrequencyInLyricsNoCountTest1() throws Exception {
        try (MockedStatic<LyricsRequest> mockedLyrics = mockStatic(LyricsRequest.class);
             MockedStatic<LyricsProcessing> mockedProcessing = mockStatic(LyricsProcessing.class)) {

            SpotifyRequest spotReq = mock(SpotifyRequest.class);
            List<String> songs = new ArrayList<>();
            songs.add("song");
            when(spotReq.getAllSongsForArtist("artist")).thenReturn(songs);

            setupLyricsRequest(mockedLyrics, "song", "artist", "Yes");

            List<String> processedLyrics = buildProcessedLyrics();
            mockedProcessing.when(() -> LyricsProcessing.processLyrics("Yes"))
                    .thenReturn(processedLyrics);

            WordCloudService service = new WordCloudService(spotReq);
            service.setSongsForUser("jeena", songs);
            List<String> artists = new ArrayList<>();
            artists.add("artist");
            service.setArtistsForUser("jeena", artists);
            List<SongFrequency> output = service.getSongFrequencyInLyricsNoCount("Yes", "artist", 1, "jeena");

            assertEquals(1, output.size());
            assertEquals("song", output.get(0).getSong());
            assertEquals(1, output.get(0).getCount());
        }
    }

    @Test
    void getSongFrequencyInLyricsNoCountTest2() throws Exception {
        try (MockedStatic<LyricsRequest> mockedLyrics = mockStatic(LyricsRequest.class)) {

            SpotifyRequest spotReq = mock(SpotifyRequest.class);
            List<String> songs = new ArrayList<>();
            songs.add("song2");
            when(spotReq.getAllSongsForArtist("artist")).thenReturn(songs);

            setupLyricsRequest(mockedLyrics, "song2", "artist", null);

            WordCloudService service = new WordCloudService(spotReq);
            service.setSongsForUser("jeena", songs);
            List<String> artists = new ArrayList<>();
            artists.add("artist");
            service.setArtistsForUser("jeena", artists);
            List<SongFrequency> output = service.getSongFrequencyInLyricsNoCount("Yes", "artist", 1, "jeena");

            assertEquals(0, output.size());
        }
    }

    @Test
    void getSongFrequencyInLyricsNoCountTest3() throws Exception {
        try (MockedStatic<LyricsRequest> mockedLyrics = mockStatic(LyricsRequest.class);
             MockedStatic<LyricsProcessing> mockedProcessing = mockStatic(LyricsProcessing.class)) {

            SpotifyRequest spotReq = mock(SpotifyRequest.class);
            List<String> songs = new ArrayList<>();
            songs.add("song");
            songs.add("song2");
            when(spotReq.getAllSongsForArtist("artist")).thenReturn(songs);

            setupLyricsRequest(mockedLyrics, "song", "artist", "Yes");
            setupLyricsRequest(mockedLyrics, "song2", "artist", "No");

            List<String> processedLyrics = buildProcessedLyrics();
            mockedProcessing.when(() -> LyricsProcessing.processLyrics("Yes"))
                    .thenReturn(processedLyrics);

            WordCloudService service = new WordCloudService(spotReq);
            service.setSongsForUser("jeena", songs);
            List<String> artists = new ArrayList<>();
            artists.add("artist");
            artists.add("artist2");
            service.setArtistsForUser("jeena", artists);
            List<SongFrequency> output = service.getSongFrequencyInLyricsNoCount("Yes", "artist", 2, "jeena");

            assertEquals(1, output.size());
            assertEquals("song", output.get(0).getSong());
            assertEquals(1, output.get(0).getCount());
        }
    }

    @Test
    void getSongFrequencyInLyricsTest1() throws Exception {
        try (MockedStatic<LyricsRequest> mockedLyrics = mockStatic(LyricsRequest.class);
             MockedStatic<LyricsProcessing> mockedProcessing = mockStatic(LyricsProcessing.class)) {

            SpotifyRequest spotReq = mock(SpotifyRequest.class);
            List<String> songs = new ArrayList<>();
            songs.add("song");
            when(spotReq.getNumPopularSongs("artist", 1)).thenReturn(songs);

            setupLyricsRequest(mockedLyrics, "song", "artist", "Yes");

            List<String> processedLyrics = buildProcessedLyrics();
            mockedProcessing.when(() -> LyricsProcessing.processLyrics("Yes"))
                    .thenReturn(processedLyrics);

            WordCloudService service = new WordCloudService(spotReq);
            service.setSongsForUser("jeena", songs);
            List<String> artists = new ArrayList<>();
            artists.add("artist");
            service.setArtistsForUser("jeena", artists);
            List<SongFrequency> output = service.getSongFrequencyInLyrics("Yes", "artist", 1, "jeena");

            assertEquals(1, output.size());
            assertEquals("song", output.get(0).getSong());
            assertEquals(1, output.get(0).getCount());
        }
    }

    @Test
    void getSongFrequencyInLyricsTest2() throws Exception {
        try (MockedStatic<LyricsRequest> mockedLyrics = mockStatic(LyricsRequest.class)) {

            SpotifyRequest spotReq = mock(SpotifyRequest.class);
            List<String> songs = new ArrayList<>();
            songs.add("song2");
            when(spotReq.getNumPopularSongs("artist", 1)).thenReturn(songs);

            setupLyricsRequest(mockedLyrics, "song2", "artist", null);

            WordCloudService service = new WordCloudService(spotReq);
            List<SongFrequency> output = service.getSongFrequencyInLyrics("Yes", "artist", 1, "jeena");

            assertEquals(0, output.size());
        }
    }

    @Test
    void getSongFrequencyInLyricsTest3() throws Exception {
        try (MockedStatic<LyricsRequest> mockedLyrics = mockStatic(LyricsRequest.class);
             MockedStatic<LyricsProcessing> mockedProcessing = mockStatic(LyricsProcessing.class)) {

            SpotifyRequest spotReq = mock(SpotifyRequest.class);
            List<String> songs = new ArrayList<>();
            songs.add("song");
            songs.add("song2");
            when(spotReq.getNumPopularSongs("artist", 2)).thenReturn(songs);

            setupLyricsRequest(mockedLyrics, "song", "artist", "Yes");
            setupLyricsRequest(mockedLyrics, "song2", "artist", "No");

            List<String> processedLyrics = buildProcessedLyrics();
            mockedProcessing.when(() -> LyricsProcessing.processLyrics("Yes"))
                    .thenReturn(processedLyrics);

            WordCloudService service = new WordCloudService(spotReq);
            service.setSongsForUser("jeena", songs);
            List<String> artists = new ArrayList<>();
            artists.add("artist");
            artists.add("artist2");
            service.setArtistsForUser("jeena", artists);
            List<SongFrequency> output = service.getSongFrequencyInLyrics("Yes", "artist", 2, "jeena");

            assertEquals(1, output.size());
            assertEquals("song", output.get(0).getSong());
            assertEquals(1, output.get(0).getCount());
        }
    }

    @Test
    void getWordCountInSongLyricsTest() {
        List<String> songLyrics = new ArrayList<>();
        songLyrics.add("word1");
        songLyrics.add("word2");
        songLyrics.add("word3");
        String word = "word1";
        SpotifyRequest spotReq = mock(SpotifyRequest.class);
        WordCloudService service = new WordCloudService(spotReq);
        assertEquals(1, service.getWordCountInSongLyrics(songLyrics, word));
    }


    @Test
    void getSongDetailsTest() throws Exception {
        try (MockedStatic<LyricsRequest> mockedStatic = mockStatic(LyricsRequest.class)) {
            mockedStatic.when(() -> LyricsRequest.getLyricsFromTitleArtist("song", "artist"))
                    .thenReturn("Yes, crazy right");

            SpotifyRequest spotReq = mock(SpotifyRequest.class);
            when(spotReq.getReleaseDateForSong("song", "artist")).thenReturn("2004");

            WordCloudService service = new WordCloudService(spotReq);
            List<String> songs = new ArrayList<>();
            songs.add("song");
            List<String> artists = new ArrayList<>();
            artists.add("artist");
            service.setSongsForUser("jeena", songs);
            service.setArtistsForUser("jeena", artists);
            Song output = service.getSongDetails("song", "artist", "jeena");
            assertEquals("song", output.getTitle());
            assertEquals("Yes, crazy right", output.getLyrics());
        }
    }

    @Test
    void getSongDetailsFavoritesTest() throws Exception {
        try (MockedStatic<LyricsRequest> mockedStatic = mockStatic(LyricsRequest.class)) {
            mockedStatic.when(() -> LyricsRequest.getLyricsFromTitleArtist("song", "artist"))
                    .thenReturn("Yes, crazy right");

            SpotifyRequest spotReq = mock(SpotifyRequest.class);
            when(spotReq.getReleaseDateForSong("song", "artist")).thenReturn("2004");

            WordCloudService service = new WordCloudService(spotReq);

            Song output = service.getSongDetailsFavorites("song", "artist");
            assertEquals("song", output.getTitle());
            assertEquals("Yes, crazy right", output.getLyrics());
        }
    }

    @Test
    void generateWordCloudFromSongListTest() throws Exception {
        List<String> songs = new ArrayList<>();
        songs.add("song1");
        songs.add("song2");
        SpotifyRequest spotReq = mock(SpotifyRequest.class);
        try (MockedStatic<LyricsRequest> mockedStatic = mockStatic(LyricsRequest.class)) {
            mockedStatic.when(() -> LyricsRequest.getLyricsFromTitleArtist("song1", "artist"))
                    .thenReturn("Yes, crazy right");
            mockedStatic.when(() -> LyricsRequest.getLyricsFromTitleArtist("song2", "artist"))
                    .thenReturn("More lyrics");

            List<String> processedLyrics = buildProcessedLyrics();

            WordCloudService service = new WordCloudService(spotReq);
            List<WordFrequency> words = service.generateWordCloudFromSongList("artist", songs, "jeena");

            assertEquals(4, words.size());
            assertEquals(1, words.get(0).getCount());

        }
    }

    @Test
    void generateWordCloudFromSongListException() throws Exception {
        List<String> songs = new ArrayList<>();
        songs.add("song1");
        songs.add("song2");
        SpotifyRequest spotReq = mock(SpotifyRequest.class);
        try (MockedStatic<LyricsRequest> mockedStatic = mockStatic(LyricsRequest.class)) {
            mockedStatic.when(() -> LyricsRequest.getLyricsFromTitleArtist("song1", "artist"))
                    .thenThrow(new RuntimeException("error"));
            mockedStatic.when(() -> LyricsRequest.getLyricsFromTitleArtist("song2", "artist"))
                    .thenReturn("More lyrics");

            List<String> processedLyrics = buildProcessedLyrics();

            WordCloudService service = new WordCloudService(spotReq);
            List<WordFrequency> words = service.generateWordCloudFromSongList("artist", songs, "jeena");

            assertEquals(1, words.size());
        }
    }

    @Test
    void getSongsForArtistTest() throws Exception {
        List<String> songs = new ArrayList<>();
        songs.add("song1");
        SpotifyRequest spotReq = mock(SpotifyRequest.class);
        when(spotReq.getAllSongsForArtist("artist")).thenReturn(songs);
        WordCloudService service = new WordCloudService(spotReq);
        assertEquals("song1", service.getSongsForArtist("artist").get(0));
    }

    @Test
    void getAmbiguousArtistsTest() throws Exception {
        List<Item> items = new ArrayList<>();
        Item item1 = new Item();
        item1.setName("Justin Bieber");
        List<Image> images = new ArrayList<>();
        Image image1 = new Image();
        image1.setURL("fakeurl");
        images.add(image1);
        item1.setImages(images);
        items.add(item1);

        Item item2 = new Item();
        item2.setName("Justin Timberlake");
        List<Image> images2 = new ArrayList<>();
        Image image2 = new Image();
        image2.setURL("fakeurl");
        images2.add(image2);
        item2.setImages(images2);
        items.add(item2);

        SpotifyRequest spotReq = mock(SpotifyRequest.class);
        when(spotReq.getPossibleArtists("Justin")).thenReturn(items);

        WordCloudService service = new WordCloudService(spotReq);
        assertEquals(2, service.getAmbiguousArtists("Justin").size());
    }

    @Test
    void updateWordCloudFromSongListTest1() throws Exception {
        SpotifyRequest spotReq = mock(SpotifyRequest.class);
        WordCloudService service = spy(new WordCloudService(spotReq));

        String username = "user1";
        String artist = "Taylor Swift";
        List<String> newSongs = List.of("Love Story");
        String wordCloudJson = "[]";

        // Mock methods for getting and setting songs/artists
        doReturn(List.of()).when(service).getSongsForUser(username);
        doReturn(List.of()).when(service).getArtistsForUser(username);
        doNothing().when(service).setSongsForUser(anyString(), anyList());
        doNothing().when(service).setArtistsForUser(anyString(), anyList());

        try (
                MockedStatic<LyricsRequest> mockLyrics = mockStatic(LyricsRequest.class);
                MockedStatic<LyricsProcessing> mockProcessing = mockStatic(LyricsProcessing.class)
        ) {
            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Love Story", "Taylor Swift"))
                    .thenReturn("We were both young when I first saw you");

            mockProcessing.when(() -> LyricsProcessing.processLyrics("We were both young when I first saw you"))
                    .thenReturn(List.of("young", "first", "saw"));

            List<WordFrequency> result = service.updateWordCloudFromSongList(
                    artist, newSongs, username, wordCloudJson
            );

            assertNotNull(result);
            assertEquals(3, result.size());

            assertEquals("young", result.get(0).getWord());
            assertTrue(result.stream().anyMatch(wf -> wf.getWord().equals("saw")));
        }
    }

    @Test
    void updateWordCloudFromSongList_existingSongsIncluded() throws Exception {
        SpotifyRequest spotReq = mock(SpotifyRequest.class);
        WordCloudService service = spy(new WordCloudService(spotReq));

        String username = "user1";
        String artist = "Taylor Swift";
        List<String> newSongs = List.of("Love Story");
        String wordCloudJson = "[]";

        doReturn(List.of("Enchanted")).when(service).getSongsForUser(username);
        doReturn(List.of("Taylor Swift")).when(service).getArtistsForUser(username);
        doNothing().when(service).setSongsForUser(anyString(), anyList());
        doNothing().when(service).setArtistsForUser(anyString(), anyList());

        try (
                MockedStatic<LyricsRequest> mockLyrics = mockStatic(LyricsRequest.class);
                MockedStatic<LyricsProcessing> mockProcessing = mockStatic(LyricsProcessing.class)
        ) {
            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Enchanted", "Taylor Swift"))
                    .thenReturn("This night is sparkling don't you let it go");
            mockProcessing.when(() -> LyricsProcessing.processLyrics("This night is sparkling don't you let it go"))
                    .thenReturn(List.of("sparkling", "night", "go"));

            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Love Story", "Taylor Swift"))
                    .thenReturn("We were both young when I first saw you");
            mockProcessing.when(() -> LyricsProcessing.processLyrics("We were both young when I first saw you"))
                    .thenReturn(List.of("young", "first", "saw"));

            List<WordFrequency> result = service.updateWordCloudFromSongList(
                    artist, newSongs, username, wordCloudJson
            );

            assertNotNull(result);
            assertEquals(6, result.size());

            assertTrue(result.stream().anyMatch(wf -> wf.getWord().equals("sparkling")));
            assertTrue(result.stream().anyMatch(wf -> wf.getWord().equals("young")));
        }
    }

    @Test
    void getSongFrequencyInLyrics_lyricsRequestThrowsException() throws Exception {
        SpotifyRequest spotReq = mock(SpotifyRequest.class);
        WordCloudService service = spy(new WordCloudService(spotReq));

        String username = "user1";
        String artist = "Taylor Swift";
        String word = "love";

        doReturn(List.of("Enchanted")).when(service).getSongsForUser(username);
        doReturn(List.of("Taylor Swift")).when(service).getArtistsForUser(username);

        try (MockedStatic<LyricsRequest> mockLyrics = mockStatic(LyricsRequest.class)) {
            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Enchanted", "Taylor Swift"))
                    .thenThrow(new RuntimeException("Lyrics fetch failed"));
            List<SongFrequency> result = service.getSongFrequencyInLyrics(word, artist, 1, username);

            assertNotNull(result);
            assertTrue(result.isEmpty(), "Expected no song frequencies due to lyrics fetch failure");
        }
    }

    @Test
    void updateWordCloudFromSongList_lyricsRequestThrowsExceptionForExistingSong() throws Exception {
        SpotifyRequest spotReq = mock(SpotifyRequest.class);
        WordCloudService service = spy(new WordCloudService(spotReq));

        String username = "user1";
        String artist = "Taylor Swift";
        List<String> newSongs = List.of();  // we're not testing new songs here
        String wordCloudJson = "[]";

        doReturn(List.of("Blank Space", "Enchanted")).when(service).getSongsForUser(username);
        doReturn(List.of("Taylor Swift", "Taylor Swift")).when(service).getArtistsForUser(username);

        try (
                MockedStatic<LyricsRequest> mockLyrics = mockStatic(LyricsRequest.class);
                MockedStatic<LyricsProcessing> mockProcessing = mockStatic(LyricsProcessing.class)
        ) {
            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Blank Space", "Taylor Swift"))
                    .thenThrow(new RuntimeException("Lyrics not found"));

            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Enchanted", "Taylor Swift"))
                    .thenReturn("This night is sparkling, don't you let it go");

            mockProcessing.when(() -> LyricsProcessing.processLyrics("This night is sparkling, don't you let it go"))
                    .thenReturn(List.of("night", "sparkling", "let"));

            List<WordFrequency> result = service.updateWordCloudFromSongList(
                    artist, newSongs, username, wordCloudJson
            );

            assertNotNull(result);
            assertEquals(3, result.size());
            assertTrue(result.stream().anyMatch(wf -> wf.getWord().equals("sparkling")));
        }
    }

    @Test
    void updateWordCloudFromSongList_handlesNewSongException_andHitsSongsForWordBranch() throws Exception {
        SpotifyRequest spotReq = mock(SpotifyRequest.class);
        WordCloudService service = spy(new WordCloudService(spotReq));

        String username = "user1";
        String artist = "Taylor Swift";
        List<String> newSongs = List.of("Enchanted", "Invisible");
        String wordCloudJson = "[]";
        doReturn(List.of()).when(service).getSongsForUser(username);
        doReturn(List.of()).when(service).getArtistsForUser(username);

        try (
                MockedStatic<LyricsRequest> mockLyrics = mockStatic(LyricsRequest.class);
                MockedStatic<LyricsProcessing> mockProcessing = mockStatic(LyricsProcessing.class)
        ) {
            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Enchanted", "Taylor Swift"))
                    .thenReturn("This night is sparkling");
            mockProcessing.when(() -> LyricsProcessing.processLyrics("This night is sparkling"))
                    .thenReturn(List.of("night", "sparkling"));

            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Invisible", "Taylor Swift"))
                    .thenThrow(new RuntimeException("Lyrics not found"));

            List<WordFrequency> result = service.updateWordCloudFromSongList(
                    artist, newSongs, username, wordCloudJson
            );

            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.stream().anyMatch(wf -> wf.getWord().equals("sparkling")));

            verify(service).setSongsForUser(eq(username), argThat(songs -> songs.contains("Enchanted")));
            verify(service).setArtistsForUser(eq(username), any());
        }
    }

    @Test
    void updateWordCloudFromSongListWithSongCountTest() throws Exception {
        SpotifyRequest spotReq = mock(SpotifyRequest.class);
        WordCloudService service = spy(new WordCloudService(spotReq));

        String username = "user1";
        String artist = "Taylor Swift";
        int songCount = 2;
        String wordCloudJson = "[]";

        // Existing user data
        doReturn(List.of("Enchanted")).when(service).getSongsForUser(username);
        doReturn(List.of("Taylor Swift")).when(service).getArtistsForUser(username);

        // New popular songs from Spotify
        when(spotReq.getNumPopularSongs(artist, songCount)).thenReturn(List.of("Love Story", "Blank Space"));

        // Prevent state-setting methods from actually mutating any state
        doNothing().when(service).setSongsForUser(anyString(), anyList());
        doNothing().when(service).setArtistsForUser(anyString(), anyList());

        try (
                MockedStatic<LyricsRequest> mockLyrics = mockStatic(LyricsRequest.class);
                MockedStatic<LyricsProcessing> mockProcessing = mockStatic(LyricsProcessing.class)
        ) {
            // Mock lyrics fetching
            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Enchanted", "Taylor Swift"))
                    .thenReturn("This night is sparkling");
            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Love Story", "Taylor Swift"))
                    .thenReturn("We were both young when I first saw you");
            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Blank Space", "Taylor Swift"))
                    .thenReturn("Nice to meet you where you been");

            // Mock processing
            mockProcessing.when(() -> LyricsProcessing.processLyrics("This night is sparkling"))
                    .thenReturn(List.of("night", "sparkling"));
            mockProcessing.when(() -> LyricsProcessing.processLyrics("We were both young when I first saw you"))
                    .thenReturn(List.of("young", "first", "saw"));
            mockProcessing.when(() -> LyricsProcessing.processLyrics("Nice to meet you where you been"))
                    .thenReturn(List.of("nice", "meet", "been"));

            List<WordFrequency> result = service.updateWordCloudFromSongListWithSongCount(artist, songCount, username, wordCloudJson);

            assertNotNull(result);
            assertEquals(8, result.size()); // 2 (existing) + 3 (Love Story) + 3 (Blank Space)
            assertTrue(result.stream().anyMatch(wf -> wf.getWord().equals("sparkling")));
            assertTrue(result.stream().anyMatch(wf -> wf.getWord().equals("meet")));
            assertTrue(result.stream().anyMatch(wf -> wf.getWord().equals("first")));

            // Verify the internal state update calls
            verify(service).setSongsForUser(eq(username), argThat(songs -> songs.contains("Enchanted") && songs.contains("Love Story") && songs.contains("Blank Space")));
            verify(service).setArtistsForUser(eq(username), argThat(artists -> artists.stream().allMatch(a -> a.equals("Taylor Swift"))));
        }
    }

    @Test
    void updateWordCloudFromSongListWithSongCount_existingSongLyricsThrowsException() throws Exception {
        SpotifyRequest spotReq = mock(SpotifyRequest.class);
        WordCloudService service = spy(new WordCloudService(spotReq));

        String username = "user1";
        String artist = "Taylor Swift";
        int songCount = 0; // Only existing song triggers logic

        doReturn(List.of("Enchanted")).when(service).getSongsForUser(username);
        doReturn(List.of("Taylor Swift")).when(service).getArtistsForUser(username);

        doNothing().when(service).setSongsForUser(anyString(), anyList());
        doNothing().when(service).setArtistsForUser(anyString(), anyList());

        try (
                MockedStatic<LyricsRequest> mockLyrics = mockStatic(LyricsRequest.class);
        ) {
            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Enchanted", "Taylor Swift"))
                    .thenThrow(new RuntimeException("Simulated failure"));

            List<WordFrequency> result = service.updateWordCloudFromSongListWithSongCount(artist, songCount, username, "[]");

            assertNotNull(result);
            assertEquals(0, result.size()); // Since lyrics failed, no words should be added
        }
    }

    @Test
    void updateWordCloudFromSongListWithSongCount_newSongLyricsThrowsException() throws Exception {
        SpotifyRequest spotReq = mock(SpotifyRequest.class);
        WordCloudService service = spy(new WordCloudService(spotReq));

        String username = "user1";
        String artist = "Taylor Swift";
        int songCount = 1;

        doReturn(List.of()).when(service).getSongsForUser(username);
        doReturn(List.of()).when(service).getArtistsForUser(username);

        when(spotReq.getNumPopularSongs(artist, songCount)).thenReturn(List.of("Love Story"));

        doNothing().when(service).setSongsForUser(anyString(), anyList());
        doNothing().when(service).setArtistsForUser(anyString(), anyList());

        try (
                MockedStatic<LyricsRequest> mockLyrics = mockStatic(LyricsRequest.class);
        ) {
            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Love Story", "Taylor Swift"))
                    .thenThrow(new RuntimeException("Simulated failure"));

            List<WordFrequency> result = service.updateWordCloudFromSongListWithSongCount(artist, songCount, username, "[]");

            assertNotNull(result);
            assertEquals(0, result.size());
        }
    }

    @Test
    void updateWordCloudFromSongListWithSongCount_songsForWordIsNotNull() throws Exception {
        SpotifyRequest spotReq = mock();
        WordCloudService service = spy(new WordCloudService(spotReq));

        String username = "user1";
        String artist = "Taylor Swift";
        int songCount = 1;
        String wordCloudJson = "[]";

        doReturn(List.of()).when(service).getSongsForUser(username);
        doReturn(List.of()).when(service).getArtistsForUser(username);

        when(spotReq.getNumPopularSongs(artist, songCount)).thenReturn(List.of("Love Story"));

        doNothing().when(service).setSongsForUser(any(), any());
        doNothing().when(service).setArtistsForUser(any(), any());

        try (
                MockedStatic<LyricsRequest> mockLyrics = mockStatic(LyricsRequest.class);
                MockedStatic<LyricsProcessing> mockProcessing = mockStatic(LyricsProcessing.class)
        ) {
            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Love Story", "Taylor Swift"))
                    .thenReturn("We were both young when I first saw you");

            mockProcessing.when(() -> LyricsProcessing.processLyrics("We were both young when I first saw you"))
                    .thenReturn(List.of("young", "first", "saw"));

            List<WordFrequency> result = service.updateWordCloudFromSongListWithSongCount(artist, songCount, username, wordCloudJson);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.stream().anyMatch(wf -> wf.getWord().equals("young")));
        }
    }

    @Test
    void updateWordCloudFromSongListWithSongCount_songsForWordIsNull() throws Exception {
        SpotifyRequest spotReq = mock();
        WordCloudService service = spy(new WordCloudService(spotReq));

        String username = "user1";
        String artist = "Taylor Swift";
        int songCount = 1;
        String wordCloudJson = "[]";

        // Simulate empty existing song/artist lists
        doReturn(List.of()).when(service).getSongsForUser(username);
        doReturn(List.of()).when(service).getArtistsForUser(username);

        when(spotReq.getNumPopularSongs(artist, songCount)).thenReturn(List.of("Love Story"));

        // Avoid writing state
        doNothing().when(service).setSongsForUser(any(), any());
        doNothing().when(service).setArtistsForUser(any(), any());

        try (
                MockedStatic<LyricsRequest> mockLyrics = mockStatic(LyricsRequest.class);
                MockedStatic<LyricsProcessing> mockProcessing = mockStatic(LyricsProcessing.class)
        ) {
            // Simulate lyrics
            mockLyrics.when(() -> LyricsRequest.getLyricsFromTitleArtist("Love Story", "Taylor Swift"))
                    .thenReturn("fake lyrics");

            mockProcessing.when(() -> LyricsProcessing.processLyrics("fake lyrics"))
                    .thenReturn(List.of("ghostword"));

            List<WordFrequency> result = service.updateWordCloudFromSongListWithSongCount(artist, songCount, username, wordCloudJson);

            assertNotNull(result);
            assertTrue(result.stream().anyMatch(wf -> wf.getWord().equals("ghostword")));
        }
    }

}

