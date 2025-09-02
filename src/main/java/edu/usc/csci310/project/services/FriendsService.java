package edu.usc.csci310.project.services;

import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.requests.SpotifyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static edu.usc.csci310.project.requests.LyricsRequest.getLyricsFromTitleArtist;

@Service
public class FriendsService {
    private FavoritesService favoriteService;
    private final Connection connection;
    private SpotifyRequest spotReq;

    private Map<String, Map<String, List<String>>> comparisonMap = new ConcurrentHashMap<>();
    private Map<String, String> songToArtist = new ConcurrentHashMap<>();

    @Autowired
    public FriendsService(Connection connection, FavoritesService favoriteService, SpotifyRequest spotifyReq) {
        this.connection = connection;
        this.favoriteService = favoriteService;
        this.spotReq = spotifyReq;
    }

    public Map<String, Map<String, List<String>>> getComparisonMap(){
        return comparisonMap;
    }

    public Map<String, String> getSongToArtist(){
        return songToArtist;
    }

    public void addToComparisonMap(String title, String username, String friend){
        Map<String, List<String>> map = new HashMap<>();
        List<String> songs = new ArrayList<>();
        songs.add(title);
        map.put(friend, songs);
        comparisonMap.put(username, map);
    }

    public void addToSongToArtist(String title, String artist){
        songToArtist.put(title, artist);
    }


    public Map<String, List<String>> getComparisonList (String friendName, String username) throws Exception {
        if (!doesUserExist(friendName)) {
            throw new Exception("User does not exist");
        }

        if (isUserPrivate(friendName)) {
            throw new Exception("User account is private");
        }
        List<Song> mySongs = favoriteService.getFavoritesForUser(username);
        for (Song song : mySongs) {
            songToArtist.put(song.getTitle(), song.getArtist());
        }

        List<Song> friendsSongs = favoriteService.getFavoritesForUser(friendName);
        for (Song song : friendsSongs) {
            songToArtist.put(song.getTitle(), song.getArtist());
        }

        Map<String, List<String>> comparisonList;
        if (comparisonMap.containsKey(username)) {
            comparisonList = comparisonMap.get(username);
        } else {
            comparisonList = new ConcurrentHashMap<>();
            comparisonMap.put(username, comparisonList);
        }

        List<String> mySongNames = new ArrayList<>();
        for (Song song: mySongs) {
            mySongNames.add(song.getTitle());
        }

        List<String> friendsSongNames = new ArrayList<>();
        for (Song song: friendsSongs) {
            friendsSongNames.add(song.getTitle());
        }

        int counter = 0;
        for (String songName : mySongNames) {
            if (friendsSongNames.contains(songName)) {
                counter += 1;
                if (comparisonList.containsKey(songName)) {
                    comparisonList.get(songName).add(friendName);
                } else {
                    List<String> friendsSongNameList = new ArrayList<>();
                    friendsSongNameList.add(friendName);
                    friendsSongNameList.add(username);
                    comparisonList.put(songName, friendsSongNameList);

                }
            }
        }
        System.out.println("My stupid head counter is: " + counter);
        if (counter == 0){
            for (String songName: mySongNames) {
                if (comparisonList.containsKey(songName)) {
                    comparisonList.get(songName).add(username);
                } else {
                    List<String> list = new ArrayList<>();
                    list.add(username);
                    comparisonList.put(songName, list);
                }
            }

            for (String songName: friendsSongNames) {
                if (comparisonList.containsKey(songName)) {
                    comparisonList.get(songName).add(friendName);
                } else {
                    List<String> list = new ArrayList<>();
                    list.add(friendName);
                    comparisonList.put(songName, list);
                }

            }
        }
        comparisonMap.put(username, comparisonList);
        return comparisonList;
    }


    public boolean doesUserExist(String friendName) {
        String query = "SELECT COUNT(*) FROM USERS WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, friendName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // If count > 0, user exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUserPrivate(String friendName) {
        String query = "SELECT privacy FROM USERS WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, friendName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                boolean isPrivate = resultSet.getBoolean("privacy");
                return isPrivate;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void clearComparisonMapForUser(String username) {
        System.out.println("IS THIS STUPID BLOB TRIGGERING?");
        comparisonMap.remove(username);
    }

    public Song getSongDetailsFromFriendsPage (String songTitle) throws Exception {
        System.out.println("I'm querying banana for :" + songTitle);
        for (String songName: songToArtist.keySet()) {
            System.out.println(songName + " " + songToArtist.get(songName));
        }
        String artist = songToArtist.get(songTitle);
        String releaseData = spotReq.getReleaseDateForSong(songTitle, artist);
        String lyrics = getLyricsFromTitleArtist(songTitle, artist);
        return new Song(songTitle, artist, releaseData, lyrics);

    }
}
