package edu.usc.csci310.project.services;

import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.requests.LyricsRequest;
import edu.usc.csci310.project.requests.SpotifyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendsServiceTest {

    private FriendsService service;
    private SpotifyRequest mockSpotifyRequest;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        FavoritesService mockFavoritesService = mock(FavoritesService.class);
        mockSpotifyRequest = mock(SpotifyRequest.class);
        service = new FriendsService(mockConnection, mockFavoritesService, mockSpotifyRequest);
        mockResultSet = mock(ResultSet.class);
    }

    @Test
    void getComparisonList() {

    }

    @Test
    void addToComparisonMapTest(){
        service.addToComparisonMap("Forever Star", "Jeena", "Parini");
        Map<String, Map<String, List<String>>> map = service.getComparisonMap();
        assertTrue(map.containsKey("Jeena"));
    }

    @Test
    void getComparisonMapTest(){
        service.addToComparisonMap("Forever Star", "Jeena", "Parini");
        Map<String, Map<String, List<String>>> map = service.getComparisonMap();
        assertTrue(map.containsKey("Jeena"));
    }

    @Test
    void getSongToArtistTest(){
        service.addToSongToArtist("Red", "Taylor Swift");
        Map<String, String> map = service.getSongToArtist();
        assertTrue(map.containsKey("Red"));
    }

    @Test
    void addToSongToArtistTest(){
        service.addToSongToArtist("Red", "Taylor Swift");
        Map<String, String> map = service.getSongToArtist();
        assertTrue(map.containsKey("Red"));
    }

    @Test
    void doesUserExistTestSuccess() throws SQLException {
        String query = "SELECT COUNT(*) FROM USERS WHERE username = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getInt(1)).thenReturn(1);

        assertEquals(true, service.doesUserExist("Parini"));

    }

    @Test
    void doesUserExistTestFailure() throws SQLException {
        String query = "SELECT COUNT(*) FROM USERS WHERE username = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(false);

        assertEquals(false, service.doesUserExist("Parini"));

    }

    // all for does user exist
    @Test
    void doesUserExistTestException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Simulated failure"));
        boolean result = service.doesUserExist("AnyUser");
        assertFalse(result);
    }

    @Test
    void doesUserExistTestCountZero() throws SQLException {
        String query = "SELECT COUNT(*) FROM USERS WHERE username = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(0); // Simulates user does not exist

        boolean result = service.doesUserExist("NoSuchUser");
        assertFalse(result);
    }

    @Test
    void isUserPrivate() throws Exception{
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getBoolean("privacy")).thenReturn(true);
        boolean resultTrue = service.isUserPrivate("UserPrivateTrue");
        assertTrue(resultTrue);
        when(mockResultSet.getBoolean("privacy")).thenReturn(false);
        boolean resultFalse = service.isUserPrivate("UserPrivateFalse");
        assertFalse(resultFalse);
        when(mockResultSet.next()).thenReturn(false);
        boolean resultNoRow = service.isUserPrivate("UserNoResult");
        assertFalse(resultNoRow);
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Boom"));
        boolean resultException = service.isUserPrivate("UserThrows");
        assertFalse(resultException);
    }

    @Test
    void clearComparisonMapForUserTest() {
        FriendsService service = mock();
        service.addToComparisonMap("Jeena", "Forever Star", "Parini");
        service.clearComparisonMapForUser("Jeena");
        Map<String, Map<String, List<String>>> map = service.getComparisonMap();
        assertTrue(!map.containsKey("Jeena"));
    }

    @Test
    void getSongDetailsFromFriendsPage() throws Exception {
        service.addToSongToArtist("Red", "Taylor Swift");
        when(mockSpotifyRequest.getReleaseDateForSong("Red", "Taylor Swift")).thenReturn("2012");

        try (MockedStatic<LyricsRequest> mockedStatic = mockStatic(LyricsRequest.class)) {
            mockedStatic.when(() -> LyricsRequest.getLyricsFromTitleArtist("song", "artist"))
                    .thenReturn("Yes, crazy right");
            Song song = service.getSongDetailsFromFriendsPage("Red");
            assertTrue(song.getArtist().equals("Taylor Swift"));
        }
    }

    @Test
    void clearComparisonMapForUser_executesSuccessfully() {
        service.addToComparisonMap("Forever Star", "Jeena", "Parini");
        assertTrue(service.getComparisonMap().containsKey("Jeena"));

        service.clearComparisonMapForUser("Jeena");
        assertFalse(service.getComparisonMap().containsKey("Jeena"));
    }

    @Test
    void getComparisonList_sharedSongs() throws Exception {
        FavoritesService mockFavorites = mock(FavoritesService.class);
        Connection mockConn = mock(Connection.class);
        SpotifyRequest mockSpotify = mock(SpotifyRequest.class);
        FriendsService service = spy(new FriendsService(mockConn, mockFavorites, mockSpotify));

        doReturn(true).when(service).doesUserExist("friend");
        doReturn(false).when(service).isUserPrivate("friend");

        Song sharedSong = new Song("Song A", "Artist A", "", "");
        List<Song> userSongs = List.of(sharedSong);
        List<Song> friendSongs = List.of(sharedSong);

        when(mockFavorites.getFavoritesForUser("me")).thenReturn(userSongs);
        when(mockFavorites.getFavoritesForUser("friend")).thenReturn(friendSongs);

        Map<String, List<String>> result = service.getComparisonList("friend", "me");

        assertTrue(result.containsKey("Song A"));
        List<String> users = result.get("Song A");
        assertTrue(users.contains("me"));
        assertTrue(users.contains("friend"));
    }

    @Test
    void getComparisonList_userDoesNotExist() {
        FavoritesService mockFavorites = mock(FavoritesService.class);
        Connection mockConn = mock(Connection.class);
        SpotifyRequest mockSpotify = mock(SpotifyRequest.class);
        FriendsService service = spy(new FriendsService(mockConn, mockFavorites, mockSpotify));

        doReturn(false).when(service).doesUserExist("friend");

        Exception exception = assertThrows(Exception.class, () -> {
            service.getComparisonList("friend", "me");
        });

        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    void getComparisonList_userIsPrivate() {
        FavoritesService mockFavorites = mock(FavoritesService.class);
        Connection mockConn = mock(Connection.class);
        SpotifyRequest mockSpotify = mock(SpotifyRequest.class);
        FriendsService service = spy(new FriendsService(mockConn, mockFavorites, mockSpotify));

        doReturn(true).when(service).doesUserExist("friend");
        doReturn(true).when(service).isUserPrivate("friend");

        Exception exception = assertThrows(Exception.class, () -> {
            service.getComparisonList("friend", "me");
        });

        assertEquals("User account is private", exception.getMessage());
    }


    @Test
    void getComparisonList_noSharedSongs() throws Exception {
        FavoritesService mockFavorites = mock(FavoritesService.class);
        Connection mockConn = mock(Connection.class);
        SpotifyRequest mockSpotify = mock(SpotifyRequest.class);
        FriendsService service = spy(new FriendsService(mockConn, mockFavorites, mockSpotify));

        doReturn(true).when(service).doesUserExist("friend");
        doReturn(false).when(service).isUserPrivate("friend");

        Song userSong = new Song("User Song", "Artist 1", "", "");
        Song friendSong = new Song("Friend Song", "Artist 2", "", "");
        when(mockFavorites.getFavoritesForUser("me")).thenReturn(List.of(userSong));
        when(mockFavorites.getFavoritesForUser("friend")).thenReturn(List.of(friendSong));

        Map<String, List<String>> result = service.getComparisonList("friend", "me");

        assertTrue(result.containsKey("User Song"));
        assertTrue(result.get("User Song").contains("me"));

        assertTrue(result.containsKey("Friend Song"));
        assertTrue(result.get("Friend Song").contains("friend"));
    }

    @Test
    void testIsUserPrivate_SQLExceptionCaught() throws Exception {
        String friendName = "userB";

        FavoritesService mockFavorites = mock(FavoritesService.class);
        Connection mockConn = mock(Connection.class);
        SpotifyRequest mockSpotify = mock(SpotifyRequest.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        FriendsService service = spy(new FriendsService(mockConn, mockFavorites, mockSpotify));

        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        boolean result = service.isUserPrivate(friendName);

        assertFalse(result);
    }



}