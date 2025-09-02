package edu.usc.csci310.project.services;

import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.models.WordFrequency;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class SoulmateEnemyService {
    private final Connection connection;
    private final WordCloudService wordCloudService;
    private final FavoritesService favoritesService;

    @Autowired
    public SoulmateEnemyService(Connection connection, WordCloudService wordCloudService, FavoritesService favoritesService) {
        this.connection = connection;
        this.wordCloudService = wordCloudService;
        this.favoritesService = favoritesService;
    }

    public int getSimilarity(List<WordFrequency> user1, List<WordFrequency> user2) {
        int numWordsInCommon = 0;
        Set<String> wordsUser1 = new HashSet<>();
        for(WordFrequency wf : user1) {
            wordsUser1.add(wf.getWord());
        }
        Set<String> wordsUser2 = new HashSet<>();
        for(WordFrequency wf : user2) {
            wordsUser2.add(wf.getWord());
        }
        for(String word : wordsUser1) {
            if(wordsUser2.contains(word)) {
                numWordsInCommon++;
            }
        }
        return numWordsInCommon;
    }

    public List<String> getPublicUsers() throws SQLException {
        //get public user accounts
        List<String> publicUsers = new ArrayList<>();
        String getPublicUsers = "SELECT username FROM users WHERE privacy = 0";
        try (PreparedStatement checkStmt = connection.prepareStatement(getPublicUsers)) {
            ResultSet rs = checkStmt.executeQuery();
            while (rs.next()) {
                publicUsers.add(rs.getString(1));
            }
        }
        if (publicUsers.isEmpty()) {
            throw new SQLException("No public users found");
        }
        return publicUsers;
    }

    public String getSoulmateForUser(String username) throws SQLException {
        try {
            List<WordFrequency> userFavoritesWords = favoritesService.generateWordCloudFromFavoritesList(username);

            List<String> publicUsers = getPublicUsers();

            List<Pair<Integer, String>> userSimilarities = new ArrayList<>();
            //iterate through public users
            for (String name : publicUsers) {
                if(!name.equals(username)) {
                    List<WordFrequency> otherFavoritesWords = favoritesService.generateWordCloudFromFavoritesList(name);
                    int similarity = getSimilarity(userFavoritesWords, otherFavoritesWords);
                    if(similarity > 0) {
                        Pair<Integer, String> p = new ImmutablePair<>(similarity, name);
                        userSimilarities.add(p);
                    }
                }
            }
            if(userSimilarities.isEmpty()) {
                throw new SQLException("No similarities found");
            }
            userSimilarities.sort(Comparator.comparing(Pair::getLeft));
            int size = userSimilarities.size();
            String soulmate = userSimilarities.get(size - 1).getRight();
            System.out.println("Soulmate: " + soulmate);
            return soulmate;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException("No soulmate found");
        }
    }

    public String getEnemyForUser(String username) throws SQLException {
        try {
            List<WordFrequency> userFavoritesWords = favoritesService.generateWordCloudFromFavoritesList(username);

            List<String> publicUsers = getPublicUsers();

            List<Pair<Integer, String>> userSimilarities = new ArrayList<>();
            //iterate through public users
            for (String name : publicUsers) {
                if(!name.equals(username)) {
                    List<WordFrequency> otherFavoritesWords = favoritesService.generateWordCloudFromFavoritesList(name);
                    Pair<Integer, String> p = new ImmutablePair<>(getSimilarity(userFavoritesWords, otherFavoritesWords), name);
                    userSimilarities.add(p);
                }
            }
            if(userSimilarities.isEmpty()) {
                throw new SQLException("No similarities found");
            }
            userSimilarities.sort(Comparator.comparing(Pair::getLeft));
            String enemy = userSimilarities.get(0).getRight();
            return enemy;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException("No enemy found");
        }
    }

    public List<Song> getFavoriteSongs(String username) throws SQLException {
        return favoritesService.getFavoritesForUser(username);
    }
}