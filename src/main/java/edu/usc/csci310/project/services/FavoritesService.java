package edu.usc.csci310.project.services;

import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.models.WordFrequency;
import edu.usc.csci310.project.requests.AddSongToFavorites;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

import static edu.usc.csci310.project.requests.LyricsRequest.getLyricsFromTitleArtist;
import static edu.usc.csci310.project.requests.WordFrequencyRequest.getTop100WordFrequencies;
import static edu.usc.csci310.project.services.LyricsProcessing.processLyrics;

@Service
public class FavoritesService {
    private final Connection connection;
    private final WordCloudService wordCloudService;

    @Autowired
    public FavoritesService(Connection connection, WordCloudService wordCloudService) {
        this.connection = connection;
        this.wordCloudService = wordCloudService;
    }

    public int addSongToFavoritesList (AddSongToFavorites request) throws SQLException {
        // Check if the song already exists for the user
        int result = 0;
        String checkSql = "SELECT id FROM favorites WHERE song = ? AND artist = ? AND username = ?";
        //String artistName = request.getArtistName().toLowerCase();
        List<String> existingArtists = wordCloudService.getArtistsForUser(request.getUsername());
        List<String> existingSongs = wordCloudService.getSongsForUser(request.getUsername());
        int index = existingSongs.indexOf(request.getSongName());
        String artistName = existingArtists.get(index).toLowerCase();
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, request.getSongName());
            checkStmt.setString(2, artistName);
            checkStmt.setString(3, request.getUsername());

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                result = -1; // Already exists
            }
        }
        if(result == -1){
            return result;
        }





        String sql = "INSERT INTO favorites (song, artist, username) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters for the insert statement
            stmt.setString(1, request.getSongName());
            stmt.setString(2, artistName);
            stmt.setString(3, request.getUsername());

            // Execute the insert statement
            int rowsAffected = stmt.executeUpdate();

            // Check if insert was successful
            if (rowsAffected > 0) {
                // SQLite specific: Retrieve the last inserted row ID
                try (Statement statement = connection.createStatement();
                     ResultSet rs = statement.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        return rs.getInt(1); // Return the generated ID
                    } else {
                        throw new SQLException("Failed to retrieve the generated ID.");
                    }
                }
            } else {
                throw new SQLException("No rows affected during the insert.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException("Problem with connection to DB.");
        }
    }

    public List<WordFrequency> generateWordCloudFromFavoritesList(String username) throws SQLException {
        List<Song> favoritesSongs = getFavoritesForUser(username);
        List<String> artists = new ArrayList<>();
        List<String> songs = new ArrayList<>();
        for (Song song : favoritesSongs) {
            songs.add(song.getTitle());
            artists.add(song.getArtist());
        }


        wordCloudService.setSongsForUser(username, songs);
        wordCloudService.setArtistsForUser(username, artists);

        String lyricsForAllSongs = "";
        for (Song song : favoritesSongs) {
            try {
                String lyrics = getLyricsFromTitleArtist(song.getTitle(), song.getArtist());
                lyricsForAllSongs += lyrics;
                lyricsForAllSongs += "\n";
            } catch (Exception e) {
                // Log the error and continue
                System.err.println("Error retrieving lyrics for song: " + song);
                e.printStackTrace();
            }

        }

        List<String> processedLyrics  = processLyrics(lyricsForAllSongs);

        List<WordFrequency> top100 = getTop100WordFrequencies(processedLyrics);

        return top100;

    }

    public List<Song> getFavoritesForUser(String username) throws SQLException {
        List<Song> favorites = new ArrayList<>();

        String sql = "SELECT song, artist FROM favorites WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String song = rs.getString("song");
                String artist = rs.getString("artist");
                favorites.add(new Song(song, artist, "", ""));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return favorites;
    }

    public int deleteFavoritesByUsername(String username) throws SQLException {
        String sql = "DELETE FROM favorites WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException("Failed to delete favorites for username.");
        }
    }

    public int deleteSongByUsernameAndSong(String username, String song, String artist) throws SQLException {
        String sql = "DELETE FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, song);
            stmt.setString(3, artist);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException("Failed to delete this song for username.");
        }
    }

    public void moveSongUpInFavoritesList(String username, String song, String artist) throws SQLException {
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        int currId = -1;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, song);
            stmt.setString(3, artist);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currId = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException("Failed to find this song's id.");
        }
        if(currId == -1){
            return;
        }

        int idAbove = currId - 1;
        String usernameAbove = "";
        String artistAbove = "";
        String songAbove = "";

        String sql2 = "SELECT username, song, artist FROM favorites WHERE id = ?";
        try (PreparedStatement stmt2 = connection.prepareStatement(sql2)) {
            stmt2.setInt(1, idAbove);

            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) {
                usernameAbove = rs2.getString("username");
                songAbove = rs2.getString("song");
                artistAbove = rs2.getString("artist");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException("Failed to find song above.");
        }
        if(usernameAbove.isEmpty()){
            return;
        }

        try {
            String sql3 = "UPDATE favorites SET song = ?, artist = ? WHERE id = ?";
            try (PreparedStatement stmt3 = connection.prepareStatement(sql3)) {
                stmt3.setString(1, song);
                stmt3.setString(2, artist);
                stmt3.setInt(3, idAbove);
                stmt3.executeUpdate();
            }

            String sql4 = "UPDATE favorites SET song = ?, artist = ? WHERE id = ?";
            try (PreparedStatement stmt4 = connection.prepareStatement(sql4)) {
                stmt4.setString(1, songAbove);
                stmt4.setString(2, artistAbove);
                stmt4.setInt(3, currId);
                stmt4.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException("Failed to swap.");
        }
    }


    public void moveSongDownInFavoritesList(String username, String song, String artist) throws SQLException {
        String sql = "SELECT id FROM favorites WHERE username = ? AND song = ? AND artist = ?";
        int currId = -1;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, song);
            stmt.setString(3, artist);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currId = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException("Failed to find this song's id.");
        }
        if(currId == -1){
            return;
        }

        int idBelow = currId + 1;
        String usernameBelow = "";
        String artistBelow = "";
        String songBelow = "";

        String sql2 = "SELECT username, song, artist FROM favorites WHERE id = ?";
        try (PreparedStatement stmt2 = connection.prepareStatement(sql2)) {
            stmt2.setInt(1, idBelow);

            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) {
                usernameBelow = rs2.getString("username");
                songBelow = rs2.getString("song");
                artistBelow = rs2.getString("artist");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException("Failed to find song below.");
        }
        if(usernameBelow.isEmpty()){
            return;
        }

        try {
            String sql3 = "UPDATE favorites SET song = ?, artist = ? WHERE id = ?";
            try (PreparedStatement stmt3 = connection.prepareStatement(sql3)) {
                stmt3.setString(1, song);
                stmt3.setString(2, artist);
                stmt3.setInt(3, idBelow);
                stmt3.executeUpdate();
            }

            String sql4 = "UPDATE favorites SET song = ?, artist = ? WHERE id = ?";
            try (PreparedStatement stmt4 = connection.prepareStatement(sql4)) {
                stmt4.setString(1, songBelow);
                stmt4.setString(2, artistBelow);
                stmt4.setInt(3, currId);
                stmt4.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException("Failed to swap.");
        }
    }

    public List<WordFrequency> updateExistingWordCloud (String username, String wordCloudJson) throws Exception {
        List<WordFrequency> existingWordCloud = WordCloudParser.parseWordFrequencies(wordCloudJson);

        List<Song> favoritesSongs = getFavoritesForUser(username);

        Map<String, List<String>> wordToSongs = new HashMap<>();
        Map<String, List<String>> wordToArtists = new HashMap<>();
        Map<String, String> songToArtists = new HashMap<>();

        for (Song song : favoritesSongs) {
            songToArtists.put(song.getTitle(), song.getArtist());
            System.out.println("Hi: "+song.getArtist());
        }

        String lyricsForAllSongs = "";
        for (Song song : favoritesSongs) {
            try {
                String lyrics = getLyricsFromTitleArtist(song.getTitle(), song.getArtist());
                lyricsForAllSongs += lyrics;
                lyricsForAllSongs += "\n";

                List<String> processedWords = processLyrics(lyrics);

                for (String word : processedWords) {
                    wordToSongs.computeIfAbsent(word, k -> new ArrayList<>()).add(song.getTitle());
                    wordToArtists.computeIfAbsent(word, k -> new ArrayList<>()).add(song.getArtist());
                }
            } catch (Exception e) {
                // Log the error and continue
                System.err.println("Error retrieving lyrics for song: " + song);
                e.printStackTrace();
            }

        }

        List<String> existingSongs = wordCloudService.getSongsForUser(username);
        List<String> existingArtists = wordCloudService.getArtistsForUser(username);

        int l = 0;
        for (String existingSong : existingSongs) {
            songToArtists.put(existingSong, existingArtists.get(l));
            l += 1;
        }

        int j = 0;
        for (String song : existingSongs) {
            try {
                String lyrics = getLyricsFromTitleArtist(song, existingArtists.get(j));

                List<String> processedWords = processLyrics(lyrics);

                for (String word : processedWords) {
                    wordToSongs.computeIfAbsent(word, k -> new ArrayList<>()).add(song);
                    wordToArtists.computeIfAbsent(word, k -> new ArrayList<>()).add(existingArtists.get(j));
                }
            } catch (Exception e) {
                // Log the error and continue
                System.err.println("Error retrieving lyrics for song: " + song);
                e.printStackTrace();
            }

            j += 1;

        }

        List<String> processedLyrics  = processLyrics(lyricsForAllSongs);

        List<WordFrequency> top100Favorites = getTop100WordFrequencies(processedLyrics);

        List<WordFrequency> top100Overall = getTop100Overall(existingWordCloud, top100Favorites);

        List<String> songsUsed = new ArrayList<>();
        List<String> artistsUsed = new ArrayList<>();
        Set<String> seenSongs = new HashSet<>();

        for (WordFrequency wf : top100Overall) {
            String word = wf.getWord();

            List<String> songsList   = wordToSongs.getOrDefault(word, Collections.emptyList());
            List<String> artistsList = wordToArtists.getOrDefault(word, Collections.emptyList());
            int limit = Math.min(songsList.size(), artistsList.size());
            for (int i = 0; i < limit; i++) {
                String song   = songsList.get(i);
                String artist = songToArtists.get(song);
                // `add()` returns false if already seen, so this single guard replaces the old contains-check
                if (seenSongs.add(song)) {
                    songsUsed.add(song);
                    artistsUsed.add(artist);
                }
            }
        }

        System.out.println("Songs used in final word cloud: " + songsUsed);
        System.out.println("Artists used in final word cloud: " + artistsUsed);

        wordCloudService.setSongsForUser(username, songsUsed);
        wordCloudService.setArtistsForUser(username, artistsUsed);

        System.out.println("Songs stored: " + wordCloudService.getSongsForUser(username));
        System.out.println("Artists stored: " + wordCloudService.getArtistsForUser(username));

        return top100Overall;
    }

    public List<WordFrequency> getTop100Overall (List<WordFrequency> existingWordCloud, List<WordFrequency> top100Favorites){
        Map<String, Integer> frequencyMap = new HashMap<>();

        // Merge both lists into frequencyMap
        for (WordFrequency wf : existingWordCloud) {
            frequencyMap.put(wf.getWord(), frequencyMap.getOrDefault(wf.getWord(), 0) + wf.getCount());
        }


        for (WordFrequency wf : top100Favorites) {
            frequencyMap.put(wf.getWord(), frequencyMap.getOrDefault(wf.getWord(), 0) + wf.getCount());
        }

        List<WordFrequency> mergedList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            mergedList.add(new WordFrequency(entry.getKey(), entry.getValue()));
        }
        // Sort by count descending
        mergedList.sort((a, b) -> Integer.compare(b.getCount(), a.getCount()));

        // Return top 100 (or fewer if less than 100 unique words in list)
        return mergedList.subList(0, Math.min(100, mergedList.size()));
    }

    public String togglePrivacyMode (boolean privacy, String username){
        String sql = "UPDATE users SET privacy = ? WHERE username = ?";
        String result;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, privacy ? 1 : 0);
            stmt.setString(2, username);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                result = "Privacy setting updated.";
            } else {
                result = "No user found with the given username.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to update privacy setting.";
        }
        return result;
    }

    public boolean getPrivacyMode(String username) throws SQLException {
        String sql = "SELECT privacy FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // privacy column: 1 = private, 0 = public
                return rs.getInt("privacy") == 1;
            }
            throw new SQLException("User not found");
        }
    }



}