package edu.usc.csci310.project.requests;

import edu.usc.csci310.project.responses.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SpotifyRequestTest {
    private HttpClient client;
    private HttpResponse<String> response;
    private SpotifyRequest spotifyRequest;

    @BeforeEach
    public void setUp() {
        client = mock(HttpClient.class);
        response = mock(HttpResponse.class);
        spotifyRequest = spy(new SpotifyRequest(client));
    }

    @Test
    public void testConstructor() {
        SpotifyRequest spotifyRequest = new SpotifyRequest();
        assertTrue(spotifyRequest != null);
    }

    @Test
    public void testGetPossibleArtists() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        try(MockedStatic<TokenRequest> tokenReq = Mockito.mockStatic(TokenRequest.class)) {
            tokenReq.when(TokenRequest::getAccessToken).thenReturn("fake_access_token123");
            when(response.body()).thenReturn("{\"artists\": {\"items\": [{\"id\":\"06HL4z0CvFAxyc27GXpf02\",\"images\": [{\"url\":\"https://i.scdn.co/image/ab6761610000e5ebe672b5f553298dcdccb0e676\",\"height\":640,\"width\":640},{\"url\":\"https://i.scdn.co/image/ab67616100005174e672b5f553298dcdccb0e676\",\"height\":320,\"width\":320},{\"url\":\"https://i.scdn.co/image/ab6761610000f178e672b5f553298dcdccb0e676\",\"height\":160,\"width\":160}],\"name\":\"Taylor Swift\"}]}}");
            List<Item> artists = spotifyRequest.getPossibleArtists("Taylor Swift");
            assertEquals(1, artists.size());
            assertEquals("Taylor Swift", artists.get(0).getName());
            assertEquals("06HL4z0CvFAxyc27GXpf02", artists.get(0).getId());
        }
    }

    @Test
    public void testGetPossibleArtistsNoneRelevant() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        try(MockedStatic<TokenRequest> tokenReq = Mockito.mockStatic(TokenRequest.class)) {
            tokenReq.when(TokenRequest::getAccessToken).thenReturn("fake_access_token123");
            when(response.body()).thenReturn("{\"artists\": {\"items\": [{\"id\":\"06HL4z0CvFAxyc27GXpf02\",\"images\": [{\"url\":\"https://i.scdn.co/image/ab6761610000e5ebe672b5f553298dcdccb0e676\",\"height\":640,\"width\":640},{\"url\":\"https://i.scdn.co/image/ab67616100005174e672b5f553298dcdccb0e676\",\"height\":320,\"width\":320},{\"url\":\"https://i.scdn.co/image/ab6761610000f178e672b5f553298dcdccb0e676\",\"height\":160,\"width\":160}],\"name\":\"Taylor Swift\"}]}}");
            assertThrows(Exception.class, () -> spotifyRequest.getPossibleArtists("Fake Artistname"));
        }
    }

    @Test
    public void testGetManyPossibleArtists() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        try(MockedStatic<TokenRequest> tokenReq = Mockito.mockStatic(TokenRequest.class)) {
            tokenReq.when(TokenRequest::getAccessToken).thenReturn("fake_access_token123");
            when(response.body()).thenReturn("{\"artists\":{\"items\":[\n" +
                    "\t\t{\"id\":\"1uNFoZAHBGtllmzznpCI3s\",\"images\":[{\"url\":\"https://i.scdn.co/image/ab6761610000e5eb8ae7f2aaa9817a704a87ea36\",\"height\":640,\"width\":640},{\"url\":\"https://i.scdn.co/image/ab676161000051748ae7f2aaa9817a704a87ea36\",\"height\":320,\"width\":320},{\"url\":\"https://i.scdn.co/image/ab6761610000f1788ae7f2aaa9817a704a87ea36\",\"height\":160,\"width\":160}],\"name\":\"Justin Bieber\"},\n" +
                    "\t\t{\"id\":\"31TPClRtHm23RisEBtV3X7\",\"images\":[{\"url\":\"https://i.scdn.co/image/ab6761610000e5eb7a5cfe2597665a3d160e805e\",\"height\":640,\"width\":640},{\"url\":\"https://i.scdn.co/image/ab676161000051747a5cfe2597665a3d160e805e\",\"height\":320,\"width\":320},{\"url\":\"https://i.scdn.co/image/ab6761610000f1787a5cfe2597665a3d160e805e\",\"height\":160,\"width\":160}],\"name\":\"Justin Timberlake\"}]}}");
            List<Item> artists = spotifyRequest.getPossibleArtists("Justin");
            assertEquals("Justin Bieber", artists.get(0).getName());
            assertEquals("Justin Timberlake", artists.get(1).getName());
        }
    }

    @Test
    public void testGetPossibleArtistsEmpty() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        try(MockedStatic<TokenRequest> tokenReq = Mockito.mockStatic(TokenRequest.class)) {
            tokenReq.when(TokenRequest::getAccessToken).thenReturn("fake_access_token123");
            when(response.body()).thenReturn("{\"artists\":{\"items\":[]}}");
            assertThrows(Exception.class, () -> spotifyRequest.getPossibleArtists("Taylor Swift"));
        }
    }

    @Test
    public void testGetPossibleArtistsError() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(400);
        try(MockedStatic<TokenRequest> tokenReq = Mockito.mockStatic(TokenRequest.class)) {
            tokenReq.when(TokenRequest::getAccessToken).thenReturn("fake_access_token123");
            assertThrows(Exception.class, () -> spotifyRequest.getPossibleArtists("Taylor Swift"));
        }
    }

    @Test
    public void testGetNumPopularSongs() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"tracks\":{\"items\":[{\"name\":\"Cruel Summer\"}," + "\n" +
                "{\"name\":\"Lover\"}," + "{\"name\":\"Fortnight (feat. Post Malone)\"}," + "{\"name\":\"Don’t Blame Me\"}," + "{\"name\":\"august\"}," + "{\"name\":\"cardigan\"}," +  "{\"name\":\"Anti-Hero\"}," + "{\"name\":\"I Don’t Wanna Live Forever (Fifty Shades Darker)\"}," + "{\"name\":\"I Can Do It With a Broken Heart\"}," + "{\"name\":\"You Belong With Me (Taylor’s Version)\"}]}}");

        List<Item> artist = new ArrayList<Item>();
        Item i = new Item();
        i.setName("Taylor Swift");
        i.setId("123");
        i.setImages(null);
        artist.add(i);

        doReturn(artist).when(spotifyRequest).getPossibleArtists("Taylor Swift");
        try(MockedStatic<TokenRequest> tokenReq = Mockito.mockStatic(TokenRequest.class)) {
            tokenReq.when(TokenRequest::getAccessToken).thenReturn("fake_access_token123");
            List<String> songs = spotifyRequest.getNumPopularSongs("Taylor Swift", 10);
            assertEquals(10, songs.size());
            assertEquals("Cruel Summer", songs.get(0));
            assertEquals("Lover", songs.get(1));
        }
    }

    @Test
    public void testGetNumPopularSongsNoneFound() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"tracks\":{\"items\":[]}}");

        List<Item> artist = new ArrayList<Item>();
        Item i = new Item();
        i.setName("Random Artist");
        i.setId("123");
        i.setImages(null);
        artist.add(i);

        doReturn(artist).when(spotifyRequest).getPossibleArtists("Random Artist");
        try(MockedStatic<TokenRequest> tokenReq = Mockito.mockStatic(TokenRequest.class)) {
            tokenReq.when(TokenRequest::getAccessToken).thenReturn("fake_access_token123");
            assertThrows(Exception.class, () -> spotifyRequest.getNumPopularSongs("Random Artist", 10));
        }
    }

    @Test
    public void testGetNumPopularSongsManyArtists() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("");

        List<Item> artist = new ArrayList<Item>();
        Item i = new Item();
        i.setName("Taylor Swift");
        i.setId("123");
        i.setImages(null);
        Item i2 = new Item();
        i2.setName("Taylor Swift2");
        i2.setId("1234");
        i2.setImages(null);
        artist.add(i);
        artist.add(i2);

        doReturn(artist).when(spotifyRequest).getPossibleArtists("Taylor");
        assertThrows(Exception.class, () -> spotifyRequest.getNumPopularSongs("Taylor", 2));
    }

    @Test
    public void testGetNumPopularSongsError() throws Exception{
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(400);
        assertThrows(Exception.class, () -> spotifyRequest.getNumPopularSongs("Taylor", 2));
    }

    @Test
    public void testGetNumPopularSongsNoTracks() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"tracks\":[]}");

        List<Item> artist = new ArrayList<Item>();
        Item i = new Item();
        i.setName("Taylor Swift");
        i.setId("123");
        i.setImages(null);
        artist.add(i);

        doReturn(artist).when(spotifyRequest).getPossibleArtists("Taylor Swift");
        try(MockedStatic<TokenRequest> tokenReq = Mockito.mockStatic(TokenRequest.class)) {
            tokenReq.when(TokenRequest::getAccessToken).thenReturn("fake_access_token123");
            assertThrows(Exception.class, () -> spotifyRequest.getNumPopularSongs("Taylor Swift", 2));
        }
    }

    @Test
    public void testGetNumPopularSongsNumOver50() throws Exception{
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"tracks\":{\"items\":[{\"name\":\"Cruel Summer\"}," +
                "{\"name\":\"Lover\"}," + "{\"name\":\"Fortnight (feat. Post Malone)\"}," + "{\"name\":\"Don't Blame Me\"}," + "{\"name\":\"august\"}," + "{\"name\":\"cardigan\"}," +  "{\"name\":\"Anti-Hero\"}," + "{\"name\":\"I Don’t Wanna Live Forever (Fifty Shades Darker)\"}," + "{\"name\":\"I Can Do It With a Broken Heart\"}," + "{\"name\":\"You Belong With Me (Taylor’s Version)\"}," + "{\"name\":\"my tears ricochet\"}," +
                "{\"name\":\"Cruel Summer\"}," + "{\"name\":\"Fortnight (feat. Post Malone)\"}," + "{\"name\":\"us. (feat. Taylor Swift)\"}," + "{\"name\":\"I Can Do It With a Broken Heart\"}," + "{\"name\":\"Bad Blood\"}," +  "{\"name\":\"You Belong With Me\"}," + "{\"name\":\"Blank Space (Taylor's Version)\"}," + "{\"name\":\"Lover\"}," + "{\"name\":\"Love Story\"}," + "{\"name\":\"Shake It Off\"}," +
                "{\"name\":\"Cruel Summer\"}," + "{\"name\":\"Fortnight (feat. Post Malone)\"}," + "{\"name\":\"us. (feat. Taylor Swift)\"}," + "{\"name\":\"I Can Do It With a Broken Heart\"}," + "{\"name\":\"Bad Blood\"}," +  "{\"name\":\"You Belong With Me\"}," + "{\"name\":\"Blank Space (Taylor's Version)\"}," + "{\"name\":\"Lover\"}," + "{\"name\":\"Love Story\"}," + "{\"name\":\"Shake It Off\"}," +
                "{\"name\":\"Cruel Summer\"}," + "{\"name\":\"Fortnight (feat. Post Malone)\"}," + "{\"name\":\"us. (feat. Taylor Swift)\"}," + "{\"name\":\"I Can Do It With a Broken Heart\"}," + "{\"name\":\"Bad Blood\"}," +  "{\"name\":\"You Belong With Me\"}," + "{\"name\":\"Blank Space (Taylor's Version)\"}," + "{\"name\":\"Lover\"}," + "{\"name\":\"Love Story\"}," + "{\"name\":\"Shake It Off\"}," +
                "{\"name\":\"Cruel Summer\"}," + "{\"name\":\"Fortnight (feat. Post Malone)\"}," + "{\"name\":\"us. (feat. Taylor Swift)\"}," + "{\"name\":\"I Can Do It With a Broken Heart\"}," + "{\"name\":\"Bad Blood\"}," +  "{\"name\":\"You Belong With Me\"}," + "{\"name\":\"Blank Space (Taylor's Version)\"}," + "{\"name\":\"Lover\"}," + "{\"name\":\"Love Story\"}]}}");

        List<Item> artist = new ArrayList<Item>();
        Item i = new Item();
        i.setName("Taylor Swift");
        i.setId("123");
        i.setImages(null);
        artist.add(i);

        doReturn(artist).when(spotifyRequest).getPossibleArtists("Taylor Swift");
        try(MockedStatic<TokenRequest> tokenReq = Mockito.mockStatic(TokenRequest.class)) {
            tokenReq.when(TokenRequest::getAccessToken).thenReturn("fake_access_token123");
            List<String> songs = spotifyRequest.getNumPopularSongs("Taylor Swift", 51);
            assertEquals("Cruel Summer", songs.get(0));
            assertEquals(50, songs.size());
        }
    }

    @Test
    public void testGetAllSongsForArtist() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"tracks\":{\"items\":[{\"name\":\"Shake It Off\"}," +
                "{\"name\":\"Cruel Summer\"}," + "{\"name\":\"Fortnight (feat. Post Malone)\"}," + "{\"name\":\"us. (feat. Taylor Swift)\"}," + "{\"name\":\"I Can Do It With a Broken Heart\"}," + "{\"name\":\"Bad Blood\"}," +  "{\"name\":\"You Belong With Me\"}," + "{\"name\":\"Blank Space (Taylor's Version)\"}," + "{\"name\":\"Lover\"}," + "{\"name\":\"Love Story\"}," + "{\"name\":\"Shake It Off\"}," +
                "{\"name\":\"Cruel Summer\"}," + "{\"name\":\"Fortnight (feat. Post Malone)\"}," + "{\"name\":\"us. (feat. Taylor Swift)\"}," + "{\"name\":\"I Can Do It With a Broken Heart\"}," + "{\"name\":\"Bad Blood\"}," +  "{\"name\":\"You Belong With Me\"}," + "{\"name\":\"Blank Space (Taylor's Version)\"}," + "{\"name\":\"Lover\"}," + "{\"name\":\"Love Story\"}," + "{\"name\":\"Shake It Off\"}," +
                "{\"name\":\"Cruel Summer\"}," + "{\"name\":\"Fortnight (feat. Post Malone)\"}," + "{\"name\":\"us. (feat. Taylor Swift)\"}," + "{\"name\":\"I Can Do It With a Broken Heart\"}," + "{\"name\":\"Bad Blood\"}," +  "{\"name\":\"You Belong With Me\"}," + "{\"name\":\"Blank Space (Taylor's Version)\"}," + "{\"name\":\"Lover\"}," + "{\"name\":\"Love Story\"}," + "{\"name\":\"Shake It Off\"}," +
                "{\"name\":\"Cruel Summer\"}," + "{\"name\":\"Fortnight (feat. Post Malone)\"}," + "{\"name\":\"us. (feat. Taylor Swift)\"}," + "{\"name\":\"I Can Do It With a Broken Heart\"}," + "{\"name\":\"Bad Blood\"}," +  "{\"name\":\"You Belong With Me\"}," + "{\"name\":\"Blank Space (Taylor's Version)\"}," + "{\"name\":\"Lover\"}," + "{\"name\":\"Love Story\"}," + "{\"name\":\"Shake It Off\"}," +
                "{\"name\":\"Cruel Summer\"}," + "{\"name\":\"Fortnight (feat. Post Malone)\"}," + "{\"name\":\"us. (feat. Taylor Swift)\"}," + "{\"name\":\"I Can Do It With a Broken Heart\"}," + "{\"name\":\"Bad Blood\"}," +  "{\"name\":\"You Belong With Me\"}," + "{\"name\":\"Blank Space (Taylor's Version)\"}," + "{\"name\":\"Lover\"}," + "{\"name\":\"Love Story\"}]}}");

        List<Item> artist = new ArrayList<Item>();
        Item i = new Item();
        i.setName("Taylor Swift");
        i.setId("123");
        i.setImages(null);
        artist.add(i);

        doReturn(artist).when(spotifyRequest).getPossibleArtists("Taylor Swift");
        try(MockedStatic<TokenRequest> tokenReq = Mockito.mockStatic(TokenRequest.class)) {
            tokenReq.when(TokenRequest::getAccessToken).thenReturn("fake_access_token123");
            List<String> songs = spotifyRequest.getAllSongsForArtist("Taylor Swift");
            assertEquals("Shake It Off", songs.get(0));
        }
    }

    @Test
    public void testGetAllSongsForArtistMany() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("");

        List<Item> artist = new ArrayList<Item>();
        Item i = new Item();
        i.setName("Taylor Swift");
        i.setId("123");
        i.setImages(null);
        Item i2 = new Item();
        i2.setName("Taylor Swift2");
        i2.setId("1234");
        i2.setImages(null);
        artist.add(i);
        artist.add(i2);

        doReturn(artist).when(spotifyRequest).getPossibleArtists("Taylor Swift");
        assertThrows(Exception.class, () -> spotifyRequest.getAllSongsForArtist("Taylor Swift"));
    }

    @Test
    public void testGetAllSongsForArtistError() throws Exception{
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(400);
        assertThrows(Exception.class, () -> spotifyRequest.getAllSongsForArtist("Taylor"));
    }

    @Test
    public void testGetAllSongsForArtistNoTracks() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"tracks\":{\"items\":[]}}");

        List<Item> artist = new ArrayList<Item>();
        Item i = new Item();
        i.setName("Taylor Swift");
        i.setId("123");
        i.setImages(null);
        artist.add(i);

        doReturn(artist).when(spotifyRequest).getPossibleArtists("Taylor Swift");
        try(MockedStatic<TokenRequest> tokenReq = Mockito.mockStatic(TokenRequest.class)) {
            tokenReq.when(TokenRequest::getAccessToken).thenReturn("fake_access_token123");
            assertThrows(Exception.class, () -> spotifyRequest.getAllSongsForArtist("Taylor Swift"));
        }
    }

    @Test
    public void testGetReleaseDateForSong() throws Exception{
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"tracks\":{\"href\":\"https://api.spotify.com/v1/search?offset=0&limit=1&query=July%20Hozier&type=track\",\"limit\":1,\"next\":\"https://api.spotify.com/v1/search?offset=1&limit=1&query=July%20Hozier&type=track\",\"offset\":0,\"previous\":null,\"total\":804,\"items\":[{\"album\":{\"album_type\":\"single\",\"artists\":[{\"external_urls\":{\"spotify\":\"https://open.spotify.com/artist/2FXC3k01G6Gw61bmprjgqS\"},\"href\":\"https://api.spotify.com/v1/artists/2FXC3k01G6Gw61bmprjgqS\",\"id\":\"2FXC3k01G6Gw61bmprjgqS\",\"name\":\"Hozier\",\"type\":\"artist\",\"uri\":\"spotify:artist:2FXC3k01G6Gw61bmprjgqS\"}],\"available_markets\":[\"AU\",\"CA\",\"PR\",\"US\"],\"external_urls\":{\"spotify\":\"https://open.spotify.com/album/3nXgYu1LTKBflY7ES8WEdo\"},\"href\":\"https://api.spotify.com/v1/albums/3nXgYu1LTKBflY7ES8WEdo\",\"id\":\"3nXgYu1LTKBflY7ES8WEdo\",\"images\":[{\"height\":640,\"width\":640,\"url\":\"https://i.scdn.co/image/ab67616d0000b2735618d72587e177ef2e825165\"},{\"height\":300,\"width\":300,\"url\":\"https://i.scdn.co/image/ab67616d00001e025618d72587e177ef2e825165\"},{\"height\":64,\"width\":64,\"url\":\"https://i.scdn.co/image/ab67616d000048515618d72587e177ef2e825165\"}],\"is_playable\":true,\"name\":\"Unaired\",\"release_date\":\"2024-08-16\",\"release_date_precision\":\"day\",\"total_tracks\":3,\"type\":\"album\",\"uri\":\"spotify:album:3nXgYu1LTKBflY7ES8WEdo\"},\"artists\":[{\"external_urls\":{\"spotify\":\"https://open.spotify.com/artist/2FXC3k01G6Gw61bmprjgqS\"},\"href\":\"https://api.spotify.com/v1/artists/2FXC3k01G6Gw61bmprjgqS\",\"id\":\"2FXC3k01G6Gw61bmprjgqS\",\"name\":\"Hozier\",\"type\":\"artist\",\"uri\":\"spotify:artist:2FXC3k01G6Gw61bmprjgqS\"}],\"available_markets\":[\"AU\",\"CA\",\"PR\",\"US\"],\"disc_number\":1,\"duration_ms\":214426,\"explicit\":false,\"external_ids\":{\"isrc\":\"USSM12405449\"},\"external_urls\":{\"spotify\":\"https://open.spotify.com/track/6WSwffQAxNchGKFiERqsW9\"},\"href\":\"https://api.spotify.com/v1/tracks/6WSwffQAxNchGKFiERqsW9\",\"id\":\"6WSwffQAxNchGKFiERqsW9\",\"is_local\":false,\"is_playable\":true,\"name\":\"July\",\"popularity\":46,\"preview_url\":null,\"track_number\":2,\"type\":\"track\",\"uri\":\"spotify:track:6WSwffQAxNchGKFiERqsW9\"}]}}");

        List<Item> artist = new ArrayList<Item>();
        Item i = new Item();
        i.setName("Hozier");
        i.setId("1234");
        i.setImages(null);
        artist.add(i);

        doReturn(artist).when(spotifyRequest).getPossibleArtists("Hozier");
        try(MockedStatic<TokenRequest> tokenReq = Mockito.mockStatic(TokenRequest.class)) {
            tokenReq.when(TokenRequest::getAccessToken).thenReturn("fake_access_token123");

            String date = spotifyRequest.getReleaseDateForSong("July", "Hozier");
            assertEquals("2024", date);
        }
    }

    @Test
    public void testGetReleaseDateForSongError() throws Exception{
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(400);
        assertThrows(Exception.class, () -> spotifyRequest.getReleaseDateForSong("July", "Hozier"));
    }

    @Test
    public void testGetReleaseDateForSongNoTracks() throws Exception {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"tracks\":{\"items\":[]}}");

        List<Item> artist = new ArrayList<Item>();
        Item i = new Item();
        i.setName("Hozier");
        i.setId("1234");
        i.setImages(null);
        artist.add(i);

        doReturn(artist).when(spotifyRequest).getPossibleArtists("Hozier");
        try(MockedStatic<TokenRequest> tokenReq = Mockito.mockStatic(TokenRequest.class)) {
            tokenReq.when(TokenRequest::getAccessToken).thenReturn("fake_access_token123");
            assertThrows(Exception.class, () -> spotifyRequest.getReleaseDateForSong("July", "Hozier"));
        }
    }
}