package edu.usc.csci310.project.requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Properties;

public class TokenRequest {
//    private static String CLIENT_ID = "";
//    private static String CLIENT_SECRET = "";
    private static HttpClient client;

    public TokenRequest(HttpClient client) {
        this.client = client;
    }

    public TokenRequest() {
        this.client = HttpClient.newHttpClient();
    }

    public static String getAccessToken() throws Exception {
        Properties properties = new Properties();
        try(InputStream inputStream = TokenRequest.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(inputStream);
            String CLIENT_ID = properties.getProperty("client_id");
            String CLIENT_SECRET = properties.getProperty("client_secret");
            System.out.println(CLIENT_ID + " : " + CLIENT_SECRET);

            String tokenURL = "https://accounts.spotify.com/api/token";

            String requestBody = "grant_type=client_credentials";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(tokenURL))
                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpStatus.OK.value()) {
                throw new Exception("Bad response");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.body());
            String accessToken = rootNode.get("access_token").asText();
            return accessToken;
        }
    }
}
