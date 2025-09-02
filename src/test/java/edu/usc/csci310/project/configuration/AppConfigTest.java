package edu.usc.csci310.project.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppConfigTest {

    @Test
    void httpClientTest() {
        AppConfig appConfig = new AppConfig();
        assertNotNull(appConfig.httpClient());
    }
}