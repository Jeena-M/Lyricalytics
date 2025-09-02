package edu.usc.csci310.project.services;

import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.models.WordFrequency;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SoulmateEnemyServiceTest {
    @Spy
    @InjectMocks
    SoulmateEnemyService soulmateEnemySpy;

    @Test
    void getSimilarityTest() {
        Connection connection = mock();
        WordCloudService wcs = mock();
        FavoritesService fs = mock();
        List<WordFrequency> user1 = new ArrayList<>();
        WordFrequency wf1 = new WordFrequency("hello", 1);
        WordFrequency wf2 = new WordFrequency("test", 1);
        user1.add(wf1);
        user1.add(wf2);
        List<WordFrequency> user2 = new ArrayList<>();
        WordFrequency wf3 = new WordFrequency("test", 2);
        user2.add(wf3);
        SoulmateEnemyService se = new SoulmateEnemyService(connection, wcs, fs);
        assertEquals(1, se.getSimilarity(user1, user2));
    }

    @Test
    void getPublicUsersTest() throws Exception{
        Connection connection = mock();
        WordCloudService wcs = mock();
        FavoritesService fs = mock();
        PreparedStatement stmt = mock();
        String getPublicUsers = "SELECT username FROM users WHERE privacy = 0";
        when(connection.prepareStatement(getPublicUsers)).thenReturn(stmt);
        ResultSet rs = mock();
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getString(1)).thenReturn("user2");
        SoulmateEnemyService se = new SoulmateEnemyService(connection, wcs, fs);
        List<String> results = se.getPublicUsers();
        assertEquals(1, results.size());
        assertEquals("user2", results.get(0));
    }

    @Test
    void getPublicUsersNoUsersTest() throws Exception{
        Connection connection = mock();
        WordCloudService wcs = mock();
        FavoritesService fs = mock();
        PreparedStatement stmt = mock();
        String getPublicUsers = "SELECT username FROM users WHERE privacy = 0";
        when(connection.prepareStatement(getPublicUsers)).thenReturn(stmt);
        ResultSet rs = mock();
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        SoulmateEnemyService se = new SoulmateEnemyService(connection, wcs, fs);
        assertThrows(SQLException.class, () -> se.getPublicUsers());
    }

    @Test
    void getFavoriteSongsTest() throws Exception{
        Connection connection = mock();
        WordCloudService wcs = mock();
        FavoritesService fs = mock();
        List<Song> songs = new ArrayList<>();
        Song s = new Song("BLUE", "Billie Eilish", "2024", "lots of lyrics");
        songs.add(s);
        when(fs.getFavoritesForUser("user1")).thenReturn(songs);
        SoulmateEnemyService se = new SoulmateEnemyService(connection, wcs, fs);
        assertEquals("BLUE", se.getFavoriteSongs("user1").get(0).getTitle());
    }

    @Test
    void getSoulmateForUserTest() throws Exception{
        Connection connection = mock();
        WordCloudService wcs = mock();
        FavoritesService fs = mock();
        List<WordFrequency> favorites = new ArrayList<>();
        WordFrequency wf1 = new WordFrequency("hello", 1);
        WordFrequency wf2 = new WordFrequency("test", 1);
        favorites.add(wf1);
        favorites.add(wf2);
        when(fs.generateWordCloudFromFavoritesList("user1")).thenReturn(favorites);
        soulmateEnemySpy = spy(new SoulmateEnemyService(connection, wcs, fs));
        List<String> users = new ArrayList<>();
        users.add("user1");
        users.add("user2");
        users.add("user3");
        doReturn(users).when(soulmateEnemySpy).getPublicUsers();
        List<WordFrequency> favorites2 = new ArrayList<>();
        WordFrequency wf3 = new WordFrequency("lyric", 1);
        WordFrequency wf4 = new WordFrequency("test", 1);
        favorites2.add(wf3);
        favorites2.add(wf4);
        when(fs.generateWordCloudFromFavoritesList("user2")).thenReturn(favorites2);
        List<WordFrequency> favorites3 = new ArrayList<>();
        WordFrequency wf5 = new WordFrequency("song", 1);
        favorites3.add(wf5);
        when(fs.generateWordCloudFromFavoritesList("user3")).thenReturn(favorites3);
        doReturn(1).when(soulmateEnemySpy).getSimilarity(favorites, favorites2);
        doReturn(0).when(soulmateEnemySpy).getSimilarity(favorites, favorites3);
        assertEquals("user2", soulmateEnemySpy.getSoulmateForUser("user1"));
    }

    @Test
    void getSoulmateForUserEmptyTest() throws Exception{
        Connection connection = mock();
        WordCloudService wcs = mock();
        FavoritesService fs = mock();
        List<WordFrequency> favorites = new ArrayList<>();
        WordFrequency wf1 = new WordFrequency("hello", 1);
        WordFrequency wf2 = new WordFrequency("test", 1);
        favorites.add(wf1);
        favorites.add(wf2);
        when(fs.generateWordCloudFromFavoritesList("user1")).thenReturn(favorites);
        soulmateEnemySpy = spy(new SoulmateEnemyService(connection, wcs, fs));
        List<String> users = new ArrayList<>();
        users.add("user1");
        users.add("user2");
        users.add("user3");
        doReturn(users).when(soulmateEnemySpy).getPublicUsers();
        List<WordFrequency> favorites2 = new ArrayList<>();
        when(fs.generateWordCloudFromFavoritesList("user2")).thenReturn(favorites2);
        List<WordFrequency> favorites3 = new ArrayList<>();
        when(fs.generateWordCloudFromFavoritesList("user3")).thenReturn(favorites3);
        doReturn(1).when(soulmateEnemySpy).getSimilarity(favorites, favorites2);
        doReturn(0).when(soulmateEnemySpy).getSimilarity(favorites, favorites3);
        assertThrows(SQLException.class, () -> soulmateEnemySpy.getSoulmateForUser("user1"));
    }

    @Test
    void getEnemyForUserTest() throws Exception{
        Connection connection = mock();
        WordCloudService wcs = mock();
        FavoritesService fs = mock();
        List<WordFrequency> favorites = new ArrayList<>();
        WordFrequency wf1 = new WordFrequency("hello", 1);
        WordFrequency wf2 = new WordFrequency("test", 1);
        favorites.add(wf1);
        favorites.add(wf2);
        when(fs.generateWordCloudFromFavoritesList("user1")).thenReturn(favorites);
        soulmateEnemySpy = spy(new SoulmateEnemyService(connection, wcs, fs));
        List<String> users = new ArrayList<>();
        users.add("user1");
        users.add("user2");
        users.add("user3");
        doReturn(users).when(soulmateEnemySpy).getPublicUsers();
        List<WordFrequency> favorites2 = new ArrayList<>();
        WordFrequency wf3 = new WordFrequency("lyric", 1);
        WordFrequency wf4 = new WordFrequency("test", 1);
        favorites2.add(wf3);
        favorites2.add(wf4);
        when(fs.generateWordCloudFromFavoritesList("user2")).thenReturn(favorites2);
        List<WordFrequency> favorites3 = new ArrayList<>();
        WordFrequency wf5 = new WordFrequency("song", 1);
        favorites3.add(wf5);
        when(fs.generateWordCloudFromFavoritesList("user3")).thenReturn(favorites3);
        doReturn(1).when(soulmateEnemySpy).getSimilarity(favorites, favorites2);
        doReturn(0).when(soulmateEnemySpy).getSimilarity(favorites, favorites3);
        assertEquals("user3", soulmateEnemySpy.getEnemyForUser("user1"));
    }

    @Test
    void getEnemyForUserEmptyTest() throws Exception{
        Connection connection = mock();
        WordCloudService wcs = mock();
        FavoritesService fs = mock();
        List<WordFrequency> favorites = new ArrayList<>();
        WordFrequency wf1 = new WordFrequency("hello", 1);
        WordFrequency wf2 = new WordFrequency("test", 1);
        favorites.add(wf1);
        favorites.add(wf2);
        when(fs.generateWordCloudFromFavoritesList("user1")).thenReturn(favorites);
        soulmateEnemySpy = spy(new SoulmateEnemyService(connection, wcs, fs));
        List<String> users = new ArrayList<>();
        doReturn(users).when(soulmateEnemySpy).getPublicUsers();
        List<WordFrequency> favorites2 = new ArrayList<>();
        when(fs.generateWordCloudFromFavoritesList("user2")).thenReturn(favorites2);
        List<WordFrequency> favorites3 = new ArrayList<>();
        when(fs.generateWordCloudFromFavoritesList("user3")).thenReturn(favorites3);
        doReturn(1).when(soulmateEnemySpy).getSimilarity(favorites, favorites2);
        doReturn(0).when(soulmateEnemySpy).getSimilarity(favorites, favorites3);
        assertThrows(SQLException.class, () -> soulmateEnemySpy.getEnemyForUser("user1"));
    }
}
