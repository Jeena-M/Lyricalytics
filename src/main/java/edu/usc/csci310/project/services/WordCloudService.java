package edu.usc.csci310.project.services;

import edu.usc.csci310.project.models.Artist;
import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.models.SongFrequency;
import edu.usc.csci310.project.models.WordFrequency;
import edu.usc.csci310.project.requests.SpotifyRequest;
import edu.usc.csci310.project.responses.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static edu.usc.csci310.project.requests.LyricsRequest.getLyricsFromTitleArtist;
import static edu.usc.csci310.project.requests.WordFrequencyRequest.getTop100WordFrequencies;
import static edu.usc.csci310.project.services.LyricsProcessing.processLyrics;

@Service
public class WordCloudService {
    private SpotifyRequest spotReq;
    private Map<String, List<String>> userWordClouds = new ConcurrentHashMap<>();
    private Map<String, List<String>> artistWordClouds = new ConcurrentHashMap<>();

    @Autowired
    public WordCloudService(SpotifyRequest spotReq) {
        this.spotReq = spotReq;
    }

    public void setSongsForUser(String username, List<String> songs) {
        userWordClouds.put(username, songs);
    }

    public void setArtistsForUser(String username, List<String> artists) {
        artistWordClouds.put(username, artists);
    }

    public List<String> getSongsForUser(String userId) {
        return userWordClouds.getOrDefault(userId, Collections.emptyList());
    }

    public List<String> getArtistsForUser(String userId) {
        return artistWordClouds.getOrDefault(userId, Collections.emptyList());
    }
    public List<WordFrequency> generateWordCloud(String artist, int songCount, String username) throws Exception {
        //SpotifyRequest spotReq = new SpotifyRequest();
        List<String> songs = spotReq.getNumPopularSongs(artist, songCount);
        List<String> artists = new ArrayList<>(Collections.nCopies(songs.size(), artist));
        setSongsForUser(username, songs);
        setArtistsForUser(username, artists);
        String lyricsForAllSongs = "";
        for (String song : songs) {
            try {
                String lyrics = getLyricsFromTitleArtist(song, artist);
                lyricsForAllSongs += lyrics;
                lyricsForAllSongs += "\n";
            } catch (Exception e) {
                // Log the error and continue
                System.err.println("Error retrieving lyrics for song: " + song);
                e.printStackTrace();
            }
        }

        List<String> processedLyrics  = processLyrics(lyricsForAllSongs);
        System.out.println(processedLyrics);

        List<WordFrequency> top100 = getTop100WordFrequencies(processedLyrics);


        return top100;

    }

    public List<WordFrequency> generateWordCloudNoNumber(String artist, String username) throws Exception {
        //SpotifyRequest spotReq = new SpotifyRequest();
        List<String> songs = spotReq.getAllSongsForArtist(artist);
        List<String> artists = new ArrayList<>(Collections.nCopies(songs.size(), artist));
        setSongsForUser(username, songs);
        setArtistsForUser(username, artists);

        String lyricsForAllSongs = "";
        for (String song : songs) {
            try {
                String lyrics = getLyricsFromTitleArtist(song, artist);
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

    public List<SongFrequency> getSongFrequencyInLyricsNoCount(String word, String artist, int songCount, String username) throws Exception {
        //SpotifyRequest spotReq = new SpotifyRequest();
        List<String> songs = getSongsForUser(username);
        List<String> artists = getArtistsForUser(username);
        List<SongFrequency> songFrequencies = new ArrayList<>();

        int i = 0;
        for (String song : songs) {
            try {
                String lyrics = getLyricsFromTitleArtist(song, artists.get(i));
                List<String> processedLyrics = processLyrics(lyrics);
                int count = getWordCountInSongLyrics(processedLyrics, word);
                if (count > 0) {
                    songFrequencies.add(new SongFrequency(song, count));
                }
            } catch(Exception e) {
                System.err.println("Error retrieving lyrics for song: " + song);
                e.printStackTrace();
            }

            i += 1;
        };

        return songFrequencies;

    }

    public List<SongFrequency> getSongFrequencyInLyrics(String word, String artist, int songCount, String username) throws Exception {
        //SpotifyRequest spotReq = new SpotifyRequest();
        List<String> songs2 = getSongsForUser(username);
        List<String> artists2 = getArtistsForUser(username);
        List<SongFrequency> songFrequencies2 = new ArrayList<>();

        int j = 0;
        for (String song2 : songs2) {
            try {
                String lyrics2 = getLyricsFromTitleArtist(song2, artists2.get(j));
                List<String> processedLyrics2 = processLyrics(lyrics2);
                int num = getWordCountInSongLyrics(processedLyrics2, word);
                if (num > 0) {
                    songFrequencies2.add(new SongFrequency(song2, num));
                }
            } catch(Exception e) {
                System.err.println("Failed to retrieve lyrics for the song: " + song2);
                e.printStackTrace();
            }
            j += 1;
        };

        return songFrequencies2;

    }

    public int getWordCountInSongLyrics(List<String> songLyrics, String word) {
        int countForWord = 0;
        for (String songWord : songLyrics) {
            if (songWord.equals(word)) {
                countForWord += 1;
            }
        }
        return countForWord;
    }

    public Song getSongDetails(String songName, String artist, String username) throws Exception {
        //SpotifyRequest spotReq = new SpotifyRequest();
        System.out.println(username);
        List<String> s = getSongsForUser(username);
        List<String> a = getArtistsForUser(username);
        int index = getSongsForUser(username).indexOf(songName);
        String artistName = getArtistsForUser(username).get(index);
        String releaseData = spotReq.getReleaseDateForSong(songName, artistName);
        String lyrics = getLyricsFromTitleArtist(songName, artistName);
        return new Song(songName, artistName, releaseData, lyrics);
    }

    public List<Artist> getAmbiguousArtists(String artistName) throws Exception {
        List<Item> items = spotReq.getPossibleArtists(artistName);
        List<Artist> artists = new ArrayList<>();
        for (Item item : items) {
            artists.add(new Artist(item.getName(), item.getImages().get(0).getURL()));
        }
        return artists;
    }

    public List<String> getSongsForArtist(String artistName) throws Exception {
        List<String> songs = spotReq.getAllSongsForArtist(artistName);
        return songs;
    }

    public List<WordFrequency> generateWordCloudFromSongList(String artist, List<String> songs, String username) throws Exception {
        //SpotifyRequest spotReq = new SpotifyRequest();
        String lyricsForAllSongs = "";
        for (String song : songs) {
            try {
                String lyrics = getLyricsFromTitleArtist(song, artist);
                lyricsForAllSongs += lyrics;
                lyricsForAllSongs += "\n";
            } catch (Exception e) {
                // Log the error and continue
                System.err.println("Error retrieving lyrics for song: " + song);
                e.printStackTrace();
            }

        }

        setSongsForUser(username, songs);
        List<String> artists = new ArrayList<>(Collections.nCopies(songs.size(), artist));
        setArtistsForUser(username, artists);

        List<String> processedLyrics  = processLyrics(lyricsForAllSongs);

        List<WordFrequency> top100 = getTop100WordFrequencies(processedLyrics);

        return top100;
    }

    public List<WordFrequency> updateWordCloudFromSongList(String artist, List<String> songs, String username, String wordCloudJson) throws Exception {
        //List<WordFrequency> existingWordCloud = WordCloudParser.parseWordFrequencies(wordCloudJson);
        List<String> existingSongs = getSongsForUser(username);
        List<String> existingArtists = getArtistsForUser(username);

        Map<String, Integer> frequencyMap = new HashMap<>();
        Map<String, Set<String>> wordToSongsMap = new HashMap<>();
        Map<String, String> songToArtistMap = new HashMap<>();


        for (int i = 0; i < existingSongs.size(); i++) {
            String existingSong = existingSongs.get(i);
            String existingArtist = existingArtists.get(i);
            songToArtistMap.put(existingSong, existingArtist);

            try {
                String lyrics = getLyricsFromTitleArtist(existingSong, existingArtist);
                List<String> processedLyrics = processLyrics(lyrics);
                for (String word: processedLyrics) {
                    frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
                    wordToSongsMap.computeIfAbsent(word, k -> new HashSet<>()).add(existingSong);
                }

            } catch (Exception e) {
                System.err.println("Error retrieving lyrics for existing song: " + existingSong);
                e.printStackTrace();
            }
        }

        // Process New Songs
        for (String song : songs) {
            songToArtistMap.put(song, artist);
            try {
                String lyrics = getLyricsFromTitleArtist(song, artist);

                List<String> processedLyrics = processLyrics(lyrics);
                for (String word: processedLyrics) {
                    frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
                    wordToSongsMap.computeIfAbsent(word, k -> new HashSet<>()).add(song);
                }

            } catch (Exception e) {
                // Log the error and continue
                System.err.println("Error retrieving lyrics for song: " + song);
                e.printStackTrace();
            }

        }


        List<WordFrequency> top100 = frequencyMap.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue())) // descending
                .limit(100)
                .map(entry -> new WordFrequency(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());


        List<String> finalSongsUsed = new ArrayList<>();
        Set<String> songSeen = new HashSet<>();

        for (WordFrequency wordFreq : top100) {
            Set<String> songsForWord = wordToSongsMap.get(wordFreq.getWord());

            for (String song : songsForWord) {
                if (!songSeen.contains(song)) {
                    finalSongsUsed.add(song);
                    songSeen.add(song);
                }
            }

        }

        setSongsForUser(username, finalSongsUsed);


        List<String> finalArtistsUsed = new ArrayList<>();
        for (String song : finalSongsUsed) {
            finalArtistsUsed.add(songToArtistMap.get(song));
        }
        setArtistsForUser(username, finalArtistsUsed);

        for (String song: finalSongsUsed) {
            System.out.println("MATCHA: "+song);
        }


        return top100;



    }

    public List<WordFrequency> updateWordCloudFromSongListWithSongCount (String artist, int songCount, String username, String wordCloudJson) throws Exception {
        List<String> existingSongs = getSongsForUser(username);
        List<String> existingArtists = getArtistsForUser(username);

        Map<String, Integer> frequencyMap = new HashMap<>();
        Map<String, Set<String>> wordToSongsMap = new HashMap<>();
        Map<String, String> songToArtistMap = new HashMap<>();


        for (int i = 0; i < existingSongs.size(); i++) {
            String existingSong = existingSongs.get(i);
            String existingArtist = existingArtists.get(i);
            songToArtistMap.put(existingSong, existingArtist);

            try {
                String lyrics = getLyricsFromTitleArtist(existingSong, existingArtist);
                List<String> processedLyrics = processLyrics(lyrics);
                for (String word: processedLyrics) {
                    frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
                    wordToSongsMap.computeIfAbsent(word, k -> new HashSet<>()).add(existingSong);
                }

            } catch (Exception e) {
                System.err.println("Error retrieving lyrics for existing song: " + existingSong);
                e.printStackTrace();
            }
        }


        // Process New Songs
        List<String> songs = spotReq.getNumPopularSongs(artist, songCount);
        for (String song : songs) {
            songToArtistMap.put(song, artist);
            try {
                String lyrics = getLyricsFromTitleArtist(song, artist);

                List<String> processedLyrics = processLyrics(lyrics);
                for (String word: processedLyrics) {
                    frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
                    wordToSongsMap.computeIfAbsent(word, k -> new HashSet<>()).add(song);
                }

            } catch (Exception e) {
                // Log the error and continue
                System.err.println("Error retrieving lyrics for song: " + song);
                e.printStackTrace();
            }

        }


        List<WordFrequency> top100 = frequencyMap.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue())) // descending
                .limit(100)
                .map(entry -> new WordFrequency(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());


        List<String> finalSongsUsed = new ArrayList<>();
        Set<String> songSeen = new HashSet<>();

        for (WordFrequency wordFreq : top100) {
            Set<String> songsForWord = wordToSongsMap.get(wordFreq.getWord());
            for (String song : songsForWord) {
                if (!songSeen.contains(song)) {
                    finalSongsUsed.add(song);
                    songSeen.add(song);
                }
            }
        }

        setSongsForUser(username, finalSongsUsed);


        List<String> finalArtistsUsed = new ArrayList<>();
        for (String song : finalSongsUsed) {
            finalArtistsUsed.add(songToArtistMap.get(song));
        }
        setArtistsForUser(username, finalArtistsUsed);

        for (String song: finalSongsUsed) {
            System.out.println("MATCHA: "+song);
        }


        return top100;



    }

    public Song getSongDetailsFavorites(String songName, String artist) throws Exception {
        String releaseData = spotReq.getReleaseDateForSong(songName, artist);
        String lyrics = getLyricsFromTitleArtist(songName, artist);
        return new Song(songName, artist, releaseData, lyrics);
    }

//    public static void main(String[] args) throws Exception{
//        SpotifyRequest spotifyRequest = new SpotifyRequest();
//        WordCloudService wordCloudService = new WordCloudService(spotifyRequest);
//        List<WordFrequency> top100 = wordCloudService.generateWordCloud("Taylor Swift", 1);
//        System.out.println(top100.size());
//        for(WordFrequency wf : top100){
//        System.out.println(wf.getWord() + " " + wf.getCount());
//        }
//
//        List<SongFrequency> songFrequencies = wordCloudService.getSongFrequencyInLyrics("risk", "Gracie Abrams", 10);
//        for(SongFrequency sf : songFrequencies){
//        System.out.println("Output: " + sf.getSong() + " " + sf.getCount());
//        }
//        System.out.println(songFrequencies);
//
//        Song song = wordCloudService.getSongDetails("Cruel Summer", "Taylor Swift");
//        System.out.println(song.getTitle() + " " + song.getArtist() + " " + song.getYear());
//        System.out.println(song.getLyrics());
//        List<WordFrequency> top100 = wordCloudService.generateWordCloudNoNumber("Taylor Swift");
//        for (WordFrequency wf : top100){
//        System.out.println(wf.getWord() + " " + wf.getCount());
//        }
//        List<Artist> artists = wordCloudService.getAmbiguousArtists("Justin");
//        for(Artist artist : artists) {
//            System.out.println(artist.getName());
//        }
//    }
}