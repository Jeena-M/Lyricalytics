package edu.usc.csci310.project;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

class SpringBootAPITest {

    @Test
    void main() {
        try (MockedStatic<SpringApplication> springApplication = Mockito.mockStatic(SpringApplication.class)) {
            // Mock its static method behaviors
            String[] args = {"arg0", "arg1"};
            springApplication.when(() -> SpringApplication.run(SpringBootAPI.class, args)).thenReturn(null); // Do nothing

            SpringBootAPI springBootAPI = new SpringBootAPI();
            springBootAPI.main(args);

            springApplication.verify(() -> SpringApplication.run(SpringBootAPI.class, args), times(1));

        }
    }

    @Test
    void redirect() {
        SpringBootAPI api = new SpringBootAPI();
        assertEquals("forward:/", api.redirect());
    }
}