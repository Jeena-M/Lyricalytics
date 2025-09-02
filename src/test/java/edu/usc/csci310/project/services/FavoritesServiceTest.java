package edu.usc.csci310.project.services;

import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.models.WordFrequency;
import edu.usc.csci310.project.requests.AddSongToFavorites;
import edu.usc.csci310.project.requests.LyricsRequest;
import edu.usc.csci310.project.requests.WordFrequencyRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Spy;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FavoritesServiceTest {
    @Spy
    @InjectMocks
    FavoritesService favoritesServiceSpy;

//    @Test
//    public void addSongToFavoritesAlreadyExists() throws SQLException {
//        Connection connection = mock();
//        WordCloudService wcService = mock(WordCloudService.class);
//        PreparedStatement statement = mock();
//
//    //        when(wcService.getArtistsForUser("user")).thenReturn()
//
//        String sql = "SELECT id FROM favorites WHERE song = ? AND artist = ? AND username = ?";
//        when(connection.prepareStatement(sql)).thenReturn(statement);
//
//        ResultSet resultSet = mock();
//        when(statement.executeQuery()).thenReturn(resultSet);
//        when(resultSet.next()).thenReturn(true);
//
//        FavoritesService favoritesService = new FavoritesService(connection, wcService);
//        AddSongToFavorites request = new AddSongToFavorites();
//        request.setUsername("user");
//        assertEquals(-1, favoritesService.addSongToFavoritesList(request));
//    }

    @Test
    public void addSongToFavoritesSuccess() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);

        List<String> songs = new ArrayList<>();
        songs.add("song");

        List<String> artists = new ArrayList<>();
        artists.add("artist");

        when(wcService.getSongsForUser("jeena")).thenReturn(songs);
        when(wcService.getArtistsForUser("jeena")).thenReturn(artists);
        PreparedStatement statement = mock();

        String sql = "SELECT id FROM favorites WHERE song = ? AND artist = ? AND username = ?";
        when(connection.prepareStatement(sql)).thenReturn(statement);

        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        String sqlInsert = "INSERT INTO favorites (song, artist, username) VALUES (?, ?, ?)";
        when(connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        Statement stmt = mock();
        when(connection.createStatement()).thenReturn(stmt);
        ResultSet resultSet1 = mock();
        when(stmt.executeQuery("SELECT last_insert_rowid()")).thenReturn(resultSet1);
        when(resultSet1.next()).thenReturn(true);
        when(resultSet1.getInt(1)).thenReturn(1);

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        AddSongToFavorites request = new AddSongToFavorites();
        request.setUsername("jeena");
        request.setSongName("song");
        request.setArtistName("artist");
        assertEquals(1, favoritesService.addSongToFavoritesList(request));
    }



    @Test
    public void addSongToFavoritesNoRowsAffected() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);

        List<String> songs = new ArrayList<>();
        songs.add("song");

        List<String> artists = new ArrayList<>();
        artists.add("artist");

        when(wcService.getSongsForUser("jeena")).thenReturn(songs);
        when(wcService.getArtistsForUser("jeena")).thenReturn(artists);
        PreparedStatement statement = mock();

        String sql = "SELECT id FROM favorites WHERE song = ? AND artist = ? AND username = ?";
        when(connection.prepareStatement(sql)).thenReturn(statement);

        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        String sqlInsert = "INSERT INTO favorites (song, artist, username) VALUES (?, ?, ?)";
        when(connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        Statement stmt = mock();
        when(connection.createStatement()).thenReturn(stmt);

        ResultSet resultSet1 = mock();
        when(stmt.executeQuery("SELECT last_insert_rowid()")).thenReturn(resultSet1);

        when(resultSet1.next()).thenReturn(false);


        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        AddSongToFavorites request = new AddSongToFavorites();
        request.setUsername("jeena");
        request.setSongName("song");
        request.setArtistName("artist");
        assertThrows(SQLException.class, () -> favoritesService.addSongToFavoritesList(request));
    }

    @Test
    public void addSongToFavoritesNoRowsAffected2() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);

        List<String> songs = new ArrayList<>();
        songs.add("song");

        List<String> artists = new ArrayList<>();
        artists.add("artist");

        when(wcService.getSongsForUser("jeena")).thenReturn(songs);
        when(wcService.getArtistsForUser("jeena")).thenReturn(artists);
        PreparedStatement statement = mock();

        String sql = "SELECT id FROM favorites WHERE song = ? AND artist = ? AND username = ?";
        when(connection.prepareStatement(sql)).thenReturn(statement);

        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        String sqlInsert = "INSERT INTO favorites (song, artist, username) VALUES (?, ?, ?)";
        when(connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(0);

        Statement stmt = mock();
        when(connection.createStatement()).thenReturn(stmt);

        ResultSet resultSet1 = mock();
        when(stmt.executeQuery("SELECT last_insert_rowid()")).thenReturn(resultSet1);

        when(resultSet1.next()).thenReturn(false);


        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        AddSongToFavorites request = new AddSongToFavorites();
        request.setUsername("jeena");
        request.setSongName("song");
        request.setArtistName("artist");
        assertThrows(SQLException.class, () -> favoritesService.addSongToFavoritesList(request));
    }

//    @Test
//    public void addSongToFavoritesNoID() throws SQLException {
//        Connection connection = mock();
//        WordCloudService wcService = mock(WordCloudService.class);
//        PreparedStatement statement = mock();
//
//        String sql = "SELECT id FROM favorites WHERE song = ? AND artist = ? AND username = ?";
//        when(connection.prepareStatement(sql)).thenReturn(statement);
//
//        ResultSet resultSet = mock();
//        when(statement.executeQuery()).thenReturn(resultSet);
//        when(resultSet.next()).thenReturn(false);
//
//        String sqlInsert = "INSERT INTO favorites (song, artist, username) VALUES (?, ?, ?)";
//        when(connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)).thenReturn(statement);
//        when(statement.executeUpdate()).thenReturn(1);
//
//        Statement stmt = mock();
//        when(connection.createStatement()).thenReturn(stmt);
//        ResultSet resultSet1 = mock();
//        when(stmt.executeQuery("SELECT last_insert_rowid()")).thenReturn(resultSet1);
//        when(resultSet1.next()).thenReturn(false);
//        when(resultSet1.getInt(1)).thenReturn(1);
//
//        FavoritesService favoritesService = new FavoritesService(connection, wcService);
//        AddSongToFavorites request = new AddSongToFavorites();
//        assertThrows(SQLException.class, () -> favoritesService.addSongToFavoritesList(request));
//    }

    @Test
    public void addSongToFavoritesAlreadyExistsException() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);

        List<String> songs = new ArrayList<>();
        songs.add("song");

        List<String> artists = new ArrayList<>();
        artists.add("artist");

        when(wcService.getSongsForUser("jeena")).thenReturn(songs);
        when(wcService.getArtistsForUser("jeena")).thenReturn(artists);
        PreparedStatement statement = mock();

        String sql = "SELECT id FROM favorites WHERE song = ? AND artist = ? AND username = ?";
        when(connection.prepareStatement(sql)).thenReturn(statement);

        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        AddSongToFavorites request = new AddSongToFavorites();
        request.setUsername("jeena");
        request.setSongName("song");
        request.setArtistName("artist");
        assertEquals(-1, favoritesService.addSongToFavoritesList(request));
    }

    @Test
    public void addSongToFavoritesAlreadyException() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);

        List<String> songs = new ArrayList<>();
        songs.add("song");

        List<String> artists = new ArrayList<>();
        artists.add("artist");

        when(wcService.getSongsForUser("jeena")).thenReturn(songs);
        when(wcService.getArtistsForUser("jeena")).thenReturn(artists);
        PreparedStatement statement = mock();

        String sql = "SELECT id FROM favorites WHERE song = ? AND artist = ? AND username = ?";
        when(connection.prepareStatement(sql)).thenReturn(statement);

        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        AddSongToFavorites request = new AddSongToFavorites();
        request.setUsername("jeena");
        request.setSongName("song");
        request.setArtistName("artist");
        assertThrows(Exception.class, () -> favoritesService.addSongToFavoritesList(request));
    }

    @Test
    public void getFavoritesForUserSuccess() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT song, artist FROM favorites WHERE username = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)).thenReturn(statement);

        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("song")).thenReturn("song1");
        when(resultSet.getString("artist")).thenReturn("artist1");


        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertEquals("song1", favoritesService.getFavoritesForUser("username").get(0).getTitle());
    }

    @Test
    public void getFavoritesForUserException() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT song, artist FROM favorites WHERE username = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)).thenReturn(statement);

        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("song")).thenReturn("song1");
        when(resultSet.getString("artist")).thenReturn("artist1");


        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertEquals(0, favoritesService.getFavoritesForUser("username").size());
    }

    @Test
    public void deleteFavoritesByUsernameSuccess() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "DELETE FROM favorites WHERE username = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);
        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertEquals(1, favoritesService.deleteFavoritesByUsername("username"));
    }

    @Test
    public void deleteFavoritesByUsernameException() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "DELETE FROM favorites WHERE username = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(new SQLException());
        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertThrows(SQLException.class, () -> favoritesService.deleteFavoritesByUsername("username"));
    }

    @Test
    public void deleteSongByUsernameAndSongSuccess() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "DELETE FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);
        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertEquals(1, favoritesService.deleteSongByUsernameAndSong("username", "song", "artist"));
    }

    @Test
    public void deleteSongByUsernameAndSongException() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "DELETE FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(new SQLException());
        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertThrows(SQLException.class, () -> favoritesService.deleteSongByUsernameAndSong("username", "song", "artist"));
    }

    @Test
    public void moveSongUpSuccess() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);

        String sql2 = "SELECT username, song, artist FROM favorites WHERE id = ?";
        PreparedStatement statement2 = mock();
        when(connection.prepareStatement(sql2)).thenReturn(statement2);
        ResultSet resultSet2 = mock();
        when(statement2.executeQuery()).thenReturn(resultSet2);
        when(resultSet2.next()).thenReturn(true);
        when(resultSet2.getString("username")).thenReturn("zoe");

        String sql3 = "UPDATE favorites SET song = ?, artist = ? WHERE id = ?";
        PreparedStatement statement3 = mock();
        when(connection.prepareStatement(sql3)).thenReturn(statement3);

        String sql4 = "UPDATE favorites SET song = ?, artist = ? WHERE id = ?";
        PreparedStatement statement4 = mock();
        when(connection.prepareStatement(sql4)).thenReturn(statement4);

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertDoesNotThrow(() -> favoritesService.moveSongUpInFavoritesList("username", "song", "artist"));
    }

    @Test
    public void moveSongUpReturn1() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertDoesNotThrow(() -> favoritesService.moveSongUpInFavoritesList("username", "song", "artist"));
    }

    @Test
    public void moveSongUpReturn2() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        String sql2 = "SELECT username, song, artist FROM favorites WHERE id = ?";
        PreparedStatement statement2 = mock();
        when(connection.prepareStatement(sql2)).thenReturn(statement2);
        ResultSet resultSet2 = mock();
        when(statement2.executeQuery()).thenReturn(resultSet2);
        when(resultSet2.next()).thenReturn(false);

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertDoesNotThrow(() -> favoritesService.moveSongUpInFavoritesList("username", "song", "artist"));
    }

    @Test
    public void moveSongUpException1() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenThrow(new SQLException());

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertThrows(SQLException.class, () -> favoritesService.moveSongUpInFavoritesList("username", "song", "artist"));
    }

    @Test
    public void moveSongUpException1Branch2() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenThrow(new SQLException());

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertThrows(SQLException.class, () -> favoritesService.moveSongUpInFavoritesList("username", "song", "artist"));
    }

    @Test
    public void moveSongUpException2() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        String sql2 = "SELECT username, song, artist FROM favorites WHERE id = ?";
        PreparedStatement statement2 = mock();
        when(connection.prepareStatement(sql2)).thenReturn(statement2);
        ResultSet resultSet2 = mock();
        when(statement2.executeQuery()).thenThrow(new SQLException());

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertThrows(SQLException.class, () -> favoritesService.moveSongUpInFavoritesList("username", "song", "artist"));
    }

    @Test
    public void moveSongUpException3() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);

        String sql2 = "SELECT username, song, artist FROM favorites WHERE id = ?";
        PreparedStatement statement2 = mock();
        when(connection.prepareStatement(sql2)).thenReturn(statement2);
        ResultSet resultSet2 = mock();
        when(statement2.executeQuery()).thenReturn(resultSet2);
        when(resultSet2.next()).thenReturn(true);
        when(resultSet2.getString("username")).thenReturn("zoe");

        String sql3 = "UPDATE favorites SET song = ?, artist = ? WHERE id = ?";
        PreparedStatement statement3 = mock();
        when(connection.prepareStatement(sql3)).thenThrow(new SQLException());

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertThrows(SQLException.class, () -> favoritesService.moveSongUpInFavoritesList("username", "song", "artist"));
    }

    @Test
    public void moveSongDownSuccess() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);

        String sql2 = "SELECT username, song, artist FROM favorites WHERE id = ?";
        PreparedStatement statement2 = mock();
        when(connection.prepareStatement(sql2)).thenReturn(statement2);
        ResultSet resultSet2 = mock();
        when(statement2.executeQuery()).thenReturn(resultSet2);
        when(resultSet2.next()).thenReturn(true);
        when(resultSet2.getString("username")).thenReturn("zoe");

        String sql3 = "UPDATE favorites SET song = ?, artist = ? WHERE id = ?";
        PreparedStatement statement3 = mock();
        when(connection.prepareStatement(sql3)).thenReturn(statement3);

        String sql4 = "UPDATE favorites SET song = ?, artist = ? WHERE id = ?";
        PreparedStatement statement4 = mock();
        when(connection.prepareStatement(sql4)).thenReturn(statement4);

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertDoesNotThrow(() -> favoritesService.moveSongDownInFavoritesList("username", "song", "artist"));
    }

    @Test
    public void moveSongDownReturn1() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertDoesNotThrow(() -> favoritesService.moveSongDownInFavoritesList("username", "song", "artist"));
    }

    @Test
    public void moveSongDownReturn2() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        String sql2 = "SELECT username, song, artist FROM favorites WHERE id = ?";
        PreparedStatement statement2 = mock();
        when(connection.prepareStatement(sql2)).thenReturn(statement2);
        ResultSet resultSet2 = mock();
        when(statement2.executeQuery()).thenReturn(resultSet2);
        when(resultSet2.next()).thenReturn(false);

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertDoesNotThrow(() -> favoritesService.moveSongDownInFavoritesList("username", "song", "artist"));
    }

    @Test
    public void moveSongDownException1() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenThrow(new SQLException());

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertThrows(SQLException.class, () -> favoritesService.moveSongDownInFavoritesList("username", "song", "artist"));
    }

    @Test
    public void moveSongDownException2() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        String sql2 = "SELECT username, song, artist FROM favorites WHERE id = ?";
        PreparedStatement statement2 = mock();
        when(connection.prepareStatement(sql2)).thenReturn(statement2);
        ResultSet resultSet2 = mock();
        when(statement2.executeQuery()).thenThrow(new SQLException());

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertThrows(SQLException.class, () -> favoritesService.moveSongDownInFavoritesList("username", "song", "artist"));
    }

    @Test
    public void moveSongDownException3() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        ResultSet resultSet = mock();
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);

        String sql2 = "SELECT username, song, artist FROM favorites WHERE id = ?";
        PreparedStatement statement2 = mock();
        when(connection.prepareStatement(sql2)).thenReturn(statement2);
        ResultSet resultSet2 = mock();
        when(statement2.executeQuery()).thenReturn(resultSet2);
        when(resultSet2.next()).thenReturn(true);
        when(resultSet2.getString("username")).thenReturn("zoe");

        String sql3 = "UPDATE favorites SET song = ?, artist = ? WHERE id = ?";
        PreparedStatement statement3 = mock();
        when(connection.prepareStatement(sql3)).thenThrow(new SQLException());

        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertThrows(SQLException.class, () -> favoritesService.moveSongDownInFavoritesList("username", "song", "artist"));
    }

    @Test
    public void getTop100OverallSuccess() throws SQLException {
        List<WordFrequency> existingWordCloud = new ArrayList<>();
        WordCloudService wcService = mock(WordCloudService.class);
        WordFrequency wf = new WordFrequency("word", 1);
        existingWordCloud.add(wf);
        List<WordFrequency> top100Favorites = new ArrayList<>();
        WordFrequency wf2 = new WordFrequency("word2", 2);
        top100Favorites.add(wf2);
        Connection connection = mock();
        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertEquals(2, favoritesService.getTop100Overall(existingWordCloud, top100Favorites).size());
    }

    @Test
    public void togglePrivacyModeSuccess() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "UPDATE users SET privacy = ? WHERE username = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);
        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertEquals("Privacy setting updated.", favoritesService.togglePrivacyMode(false, "username"));
    }

    @Test
    public void togglePrivacyModeSuccess2() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "UPDATE users SET privacy = ? WHERE username = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);
        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertEquals("Privacy setting updated.", favoritesService.togglePrivacyMode(true, "username"));
    }

    @Test
    public void togglePrivacyModeNoUser() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "UPDATE users SET privacy = ? WHERE username = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(0);
        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertEquals("No user found with the given username.", favoritesService.togglePrivacyMode(false, "username"));
    }

    @Test
    public void togglePrivacyModeFails() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "UPDATE users SET privacy = ? WHERE username = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(new SQLException());
        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertEquals("Failed to update privacy setting.", favoritesService.togglePrivacyMode(false, "username"));
    }

    @Test
    public void togglePrivacyModeFails2() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        String sql = "UPDATE users SET privacy = ? WHERE username = ?";
        PreparedStatement statement = mock();
        when(connection.prepareStatement(sql)).thenThrow(new SQLException());
        FavoritesService favoritesService = new FavoritesService(connection, wcService);
        assertEquals("Failed to update privacy setting.", favoritesService.togglePrivacyMode(true, "username"));
    }

    @Test
    public void generateWordCloudFromFavoritesSuccess() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        favoritesServiceSpy = spy(new FavoritesService(connection, wcService));
        List<Song> favorites = new ArrayList<>();
        Song song1 = new Song("Weightless", "Arlo Parks", "2023", "lyrics");
        favorites.add(song1);
        doReturn(favorites).when(favoritesServiceSpy).getFavoritesForUser("username");
        try(MockedStatic<LyricsRequest> lyricsRequestMock = mockStatic(LyricsRequest.class)) {
            lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("Weightless", "Arlo Parks")).thenReturn("even more lyrics");

            try (MockedStatic<LyricsProcessing> lyricsProcessingMock = mockStatic(LyricsProcessing.class)) {
                List<String> words = Arrays.asList("even", "more", "lyrics");
                lyricsProcessingMock.when(() -> LyricsProcessing.processLyrics("even more lyrics\n")).thenReturn(words);

                try (MockedStatic<WordFrequencyRequest> wordFrequencyRequestMock = mockStatic(WordFrequencyRequest.class)) {
                    List<WordFrequency> wordFrequencyList = new ArrayList<>();
                    WordFrequency wf1 = new WordFrequency("even", 1);
                    WordFrequency wf2 = new WordFrequency("more", 1);
                    WordFrequency wf3 = new WordFrequency("lyrics", 1);
                    wordFrequencyList.add(wf1);
                    wordFrequencyList.add(wf2);
                    wordFrequencyList.add(wf3);
                    wordFrequencyRequestMock.when(() -> WordFrequencyRequest.getTop100WordFrequencies(words)).thenReturn(wordFrequencyList);

                    assertEquals(3, favoritesServiceSpy.generateWordCloudFromFavoritesList("username").size());
                }
            }
        }
    }

    @Test
    public void generateWordCloudFromFavoritesError() throws SQLException {
        Connection connection = mock();
        WordCloudService wcService = mock(WordCloudService.class);
        favoritesServiceSpy = spy(new FavoritesService(connection, wcService));
        List<Song> favorites = new ArrayList<>();
        Song song1 = new Song("Weightless", "Arlo Parks", "2023", "lyrics");
        favorites.add(song1);
        doReturn(favorites).when(favoritesServiceSpy).getFavoritesForUser("username");
        try(MockedStatic<LyricsRequest> lyricsRequestMock = mockStatic(LyricsRequest.class)) {
            lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("Weightless", "Arlo Parks")).thenThrow(new SQLException());

            try (MockedStatic<LyricsProcessing> lyricsProcessingMock = mockStatic(LyricsProcessing.class)) {
                List<String> words = Arrays.asList("even", "more", "lyrics");
                lyricsProcessingMock.when(() -> LyricsProcessing.processLyrics("even more lyrics\n")).thenReturn(words);

                try (MockedStatic<WordFrequencyRequest> wordFrequencyRequestMock = mockStatic(WordFrequencyRequest.class)) {
                    List<WordFrequency> wordFrequencyList = new ArrayList<>();
                    WordFrequency wf1 = new WordFrequency("even", 1);
                    WordFrequency wf2 = new WordFrequency("more", 1);
                    WordFrequency wf3 = new WordFrequency("lyrics", 1);
                    wordFrequencyList.add(wf1);
                    wordFrequencyList.add(wf2);
                    wordFrequencyList.add(wf3);
                    wordFrequencyRequestMock.when(() -> WordFrequencyRequest.getTop100WordFrequencies(words)).thenReturn(wordFrequencyList);

                    assertEquals(0, favoritesServiceSpy.generateWordCloudFromFavoritesList("username").size());
                }
            }
        }
    }

    @Test
    public void updateExistingWordCloudSuccess() throws Exception {
        String json = "{\"lyrics\": \"many words\"}";
        WordCloudService wcService = mock(WordCloudService.class);
        try(MockedStatic<WordCloudParser> wordCloudParserMock = mockStatic(WordCloudParser.class)) {
            List<WordFrequency> wordFrequencyList = new ArrayList<>();
            WordFrequency wf1 = new WordFrequency("many", 1);
            WordFrequency wf2 = new WordFrequency("words", 1);
            wordFrequencyList.add(wf1);
            wordFrequencyList.add(wf2);
            wordCloudParserMock.when(() -> WordCloudParser.parseWordFrequencies(json)).thenReturn(wordFrequencyList);

            Connection connection = mock();
            favoritesServiceSpy = spy(new FavoritesService(connection, wcService));
            List<Song> favorites = new ArrayList<>();
            Song song1 = new Song("Weightless", "Arlo Parks", "2023", "lyrics");
            favorites.add(song1);
            doReturn(favorites).when(favoritesServiceSpy).getFavoritesForUser("username");

            try(MockedStatic<LyricsRequest> lyricsRequestMock = mockStatic(LyricsRequest.class)) {
                lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("Weightless", "Arlo Parks")).thenReturn("more lyrics");

                try (MockedStatic<LyricsProcessing> lyricsProcessingMock = mockStatic(LyricsProcessing.class)) {
                    List<String> words = Arrays.asList("more", "lyrics");
                    lyricsProcessingMock.when(() -> LyricsProcessing.processLyrics("more lyrics\n")).thenReturn(words);

                    try (MockedStatic<WordFrequencyRequest> wordFrequencyRequestMock = mockStatic(WordFrequencyRequest.class)) {
                        List<WordFrequency> wordFrequencyList2 = new ArrayList<>();
                        WordFrequency wf22 = new WordFrequency("more", 1);
                        WordFrequency wf32 = new WordFrequency("lyrics", 1);
                        wordFrequencyList.add(wf22);
                        wordFrequencyList.add(wf32);
                        wordFrequencyRequestMock.when(() -> WordFrequencyRequest.getTop100WordFrequencies(words)).thenReturn(wordFrequencyList);

                        List<WordFrequency> top100Overall = new ArrayList<>();
                        top100Overall.add(wf22);
                        top100Overall.add(wf32);
                        top100Overall.add(wf1);
                        top100Overall.add(wf2);
                        doReturn(top100Overall).when(favoritesServiceSpy).getTop100Overall(wordFrequencyList, wordFrequencyList2);

                        assertEquals(4, favoritesServiceSpy.updateExistingWordCloud("username", json).size());
                    }
                }
            }
        }
    }

    @Test
    public void updateExistingWordCloudException() throws Exception {
        String json = "{\"lyrics\": \"many words\"}";
        WordCloudService wcService = mock(WordCloudService.class);
        try(MockedStatic<WordCloudParser> wordCloudParserMock = mockStatic(WordCloudParser.class)) {
            List<WordFrequency> wordFrequencyList = new ArrayList<>();
            WordFrequency wf1 = new WordFrequency("many", 1);
            WordFrequency wf2 = new WordFrequency("words", 1);
            wordFrequencyList.add(wf1);
            wordFrequencyList.add(wf2);
            wordCloudParserMock.when(() -> WordCloudParser.parseWordFrequencies(json)).thenReturn(wordFrequencyList);

            Connection connection = mock();
            favoritesServiceSpy = spy(new FavoritesService(connection, wcService));
            List<Song> favorites = new ArrayList<>();
            Song song1 = new Song("Weightless", "Arlo Parks", "2023", "lyrics");
            favorites.add(song1);
            doReturn(favorites).when(favoritesServiceSpy).getFavoritesForUser("username");

            try (MockedStatic<LyricsRequest> lyricsRequestMock = mockStatic(LyricsRequest.class)) {
                lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("Weightless", "Arlo Parks"))
                        .thenReturn("running weightless into the night");

                lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("OldSong1", "OldArtist1"))
                        .thenReturn("shine bright diamond");
                lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("OldSong2", "OldArtist2"))
                        .thenReturn("light sparkles forever");

                try (MockedStatic<LyricsProcessing> lyricsProcessingMock = mockStatic(LyricsProcessing.class)) {
                    lyricsProcessingMock.when(() -> LyricsProcessing.processLyrics("running weightless into the night"))
                            .thenReturn(List.of("running", "weightless", "night"));
                    lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("OldSong1", "OldArtist1"))
                            .thenReturn("shine bright diamond");
                    lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("OldSong2", "OldArtist2"))
                            .thenThrow(new RuntimeException("Simulated lyrics fetch failure"));
                    lyricsProcessingMock.when(() -> LyricsProcessing.processLyrics(
                            "running weightless into the night\n"
                    )).thenReturn(List.of("running", "weightless", "night"));

                    try (MockedStatic<WordFrequencyRequest> wordFreqMock = mockStatic(WordFrequencyRequest.class)) {
                        List<String> allProcessedWords = List.of("running", "weightless", "night");
                        List<WordFrequency> freqFavorites = List.of(
                                new WordFrequency("running", 1),
                                new WordFrequency("weightless", 1),
                                new WordFrequency("night", 1)
                        );
                        wordFreqMock.when(() -> WordFrequencyRequest.getTop100WordFrequencies(allProcessedWords))
                                .thenReturn(freqFavorites);

                        List<WordFrequency> merged = new ArrayList<>();
                        merged.addAll(wordFrequencyList);
                        merged.addAll(freqFavorites);

                        doReturn(merged).when(favoritesServiceSpy)
                                .getTop100Overall(wordFrequencyList, freqFavorites);

                        List<WordFrequency> result = favoritesServiceSpy.updateExistingWordCloud("username", json);

                        assertEquals(5, result.size());

                        List<String> resultWords = result.stream().map(WordFrequency::getWord).toList();
                        assertTrue(resultWords.contains("many"));
                        assertTrue(resultWords.contains("words"));
                        assertTrue(resultWords.contains("running"));
                        assertTrue(resultWords.contains("weightless"));
                        assertTrue(resultWords.contains("night"));
                    }
                }
            }
        }
    }

    @Test
    public void updateExistingWordCloudProcessesWordsIntoMaps() throws Exception {
        String json = "[{\"word\":\"many\",\"count\":1},{\"word\":\"words\",\"count\":1}]";
        WordCloudService wcService = mock(WordCloudService.class);

        try (MockedStatic<WordCloudParser> wordCloudParserMock = mockStatic(WordCloudParser.class)) {
            List<WordFrequency> wordFrequencyList = List.of(
                    new WordFrequency("many", 1),
                    new WordFrequency("words", 1)
            );
            wordCloudParserMock.when(() -> WordCloudParser.parseWordFrequencies(json))
                    .thenReturn(wordFrequencyList);

            Connection connection = mock();
            favoritesServiceSpy = spy(new FavoritesService(connection, wcService));

            Song song1 = new Song("Weightless", "Arlo Parks", "2023", "lyrics");
            doReturn(List.of(song1)).when(favoritesServiceSpy).getFavoritesForUser("username");

            doReturn(List.of("OldSong1", "OldSong2")).when(wcService).getSongsForUser("username");
            doReturn(List.of("OldArtist1", "OldArtist2")).when(wcService).getArtistsForUser("username");

            try (MockedStatic<LyricsRequest> lyricsRequestMock = mockStatic(LyricsRequest.class)) {
                lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("Weightless", "Arlo Parks"))
                        .thenReturn("running weightless into the night");

                lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("OldSong1", "OldArtist1"))
                        .thenReturn("shine bright diamond");
                lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("OldSong2", "OldArtist2"))
                        .thenReturn("light sparkles forever");

                try (MockedStatic<LyricsProcessing> lyricsProcessingMock = mockStatic(LyricsProcessing.class)) {
                    lyricsProcessingMock.when(() -> LyricsProcessing.processLyrics("running weightless into the night"))
                            .thenReturn(List.of("running", "weightless", "night"));
                    lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("OldSong1", "OldArtist1"))
                            .thenReturn("shine bright diamond");
                    lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("Weightless", "Arlo Parks"))
                            .thenThrow(new RuntimeException("Simulated error for favorite song"));

                    lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("OldSong1", "OldArtist1"))
                            .thenThrow(new RuntimeException("Simulated error for existing song 1"));

                    lyricsRequestMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("OldSong2", "OldArtist2"))
                            .thenThrow(new RuntimeException("Simulated error for existing song 2"));

                    lyricsProcessingMock.when(() -> LyricsProcessing.processLyrics(
                            "running weightless into the night\n"
                    )).thenReturn(List.of("running", "weightless", "night"));

                    lyricsProcessingMock.when(() -> LyricsProcessing.processLyrics("")).thenReturn(List.of());

                    try (MockedStatic<WordFrequencyRequest> wordFreqMock = mockStatic(WordFrequencyRequest.class)) {
                        List<String> allProcessedWords = List.of("running", "weightless", "night");
                        List<WordFrequency> freqFavorites = List.of(
                                new WordFrequency("running", 1),
                                new WordFrequency("weightless", 1),
                                new WordFrequency("night", 1)
                        );
                        wordFreqMock.when(() -> WordFrequencyRequest.getTop100WordFrequencies(allProcessedWords))
                                .thenReturn(freqFavorites);

                        List<WordFrequency> merged = new ArrayList<>();
                        merged.addAll(wordFrequencyList);
                        merged.addAll(freqFavorites);

                        doReturn(merged).when(favoritesServiceSpy)
                                .getTop100Overall(wordFrequencyList, freqFavorites);

                        List<WordFrequency> result = favoritesServiceSpy.updateExistingWordCloud("username", json);

                        assertEquals(2, result.size());

                        List<String> resultWords = result.stream().map(WordFrequency::getWord).toList();
                        assertTrue(resultWords.contains("many"));
                        assertTrue(resultWords.contains("words"));
//                        assertTrue(resultWords.contains("running"));
//                        assertTrue(resultWords.contains("weightless"));
//                        assertTrue(resultWords.contains("night"));
                    }
                }
            }
        }
    }


    @Test
    public void updateExistingWordCloud_CoversExistingSongsLoopWithTryAndCatchPaths() throws Exception {
        String json = "[{\"word\":\"alpha\",\"count\":1}]";
        WordCloudService wcService = mock(WordCloudService.class);

        try (MockedStatic<WordCloudParser> parserMock = mockStatic(WordCloudParser.class)) {
            parserMock.when(() -> WordCloudParser.parseWordFrequencies(json))
                    .thenReturn(List.of(new WordFrequency("alpha", 1)));

            Connection conn = mock();
            favoritesServiceSpy = spy(new FavoritesService(conn, wcService));

            Song favoriteSong = new Song("NewSong", "NewArtist", "2024", "lyrics");
            doReturn(List.of(favoriteSong)).when(favoritesServiceSpy).getFavoritesForUser("username");

            List<String> existingSongs = List.of("OldSongSuccess", "OldSongFail");
            List<String> existingArtists = List.of("Artist1", "Artist2");
            doReturn(existingSongs).when(wcService).getSongsForUser("username");
            doReturn(existingArtists).when(wcService).getArtistsForUser("username");

            try (
                    MockedStatic<LyricsRequest> lyricsMock = mockStatic(LyricsRequest.class);
                    MockedStatic<LyricsProcessing> processingMock = mockStatic(LyricsProcessing.class);
                    MockedStatic<WordFrequencyRequest> freqMock = mockStatic(WordFrequencyRequest.class)
            ) {
                lyricsMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("NewSong", "NewArtist"))
                        .thenReturn("light up the world");
                processingMock.when(() -> LyricsProcessing.processLyrics("light up the world"))
                        .thenReturn(List.of("light", "world"));

                lyricsMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("OldSongSuccess", "Artist1"))
                        .thenReturn("brighter days ahead");
                processingMock.when(() -> LyricsProcessing.processLyrics("brighter days ahead"))
                        .thenReturn(List.of("brighter", "days"));

                lyricsMock.when(() -> LyricsRequest.getLyricsFromTitleArtist("OldSongFail", "Artist2"))
                        .thenThrow(new RuntimeException("Mocked failure"));

                freqMock.when(() -> WordFrequencyRequest.getTop100WordFrequencies(List.of("light", "world")))
                        .thenReturn(List.of(
                                new WordFrequency("light", 1),
                                new WordFrequency("world", 1)
                        ));

                doReturn(List.of(
                        new WordFrequency("alpha", 1),
                        new WordFrequency("light", 1),
                        new WordFrequency("world", 1),
                        new WordFrequency("brighter", 1),
                        new WordFrequency("days", 1)
                )).when(favoritesServiceSpy).getTop100Overall(anyList(), anyList());

                List<WordFrequency> result = favoritesServiceSpy.updateExistingWordCloud("username", json);

                assertEquals(5, result.size());
                List<String> words = result.stream().map(WordFrequency::getWord).toList();
                assertTrue(words.containsAll(List.of("alpha", "light", "world", "brighter", "days")));
            }
        }
    }

    @Test
    public void getPrivacyMode() throws Exception {
        String sql = "SELECT privacy FROM users WHERE username = ?";
        PreparedStatement stmt = mock();
        Connection connection = mock();
        when(connection.prepareStatement(sql)).thenReturn(stmt);
        ResultSet rs = mock();
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("privacy")).thenReturn(1);
        WordCloudService wcService = mock(WordCloudService.class);
        FavoritesService service = new FavoritesService(connection, wcService);
        assertTrue(service.getPrivacyMode(sql));
    }

    @Test
    public void getPrivacyModeEmpty() throws Exception {
        String sql = "SELECT privacy FROM users WHERE username = ?";
        PreparedStatement stmt = mock();
        Connection connection = mock();
        when(connection.prepareStatement(sql)).thenReturn(stmt);
        ResultSet rs = mock();
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        WordCloudService wcService = mock(WordCloudService.class);
        FavoritesService service = new FavoritesService(connection, wcService);
        assertThrows(SQLException.class, () -> service.getPrivacyMode(sql));
    }

    @Test
    void getPrivacyMode_returnsFalseWhenPrivacyIs0() throws Exception {
        Connection connection = mock();
        PreparedStatement stmt = mock();
        ResultSet rs = mock();

        WordCloudService wcService = mock(WordCloudService.class);

        when(connection.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("privacy")).thenReturn(0);  // This triggers the return false

        FavoritesService service = new FavoritesService(connection, wcService);
        boolean isPrivate = service.getPrivacyMode("jeena");

        assertFalse(isPrivate);
    }

}
