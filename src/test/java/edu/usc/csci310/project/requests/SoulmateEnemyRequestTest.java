package edu.usc.csci310.project.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SoulmateEnemyRequestTest {
    @Test
    void setRequestUsername() {
        SoulmateEnemyRequest soulmateEnemy = new SoulmateEnemyRequest();
        soulmateEnemy.setUsername("username");
        assertEquals("username", soulmateEnemy.getUsername());
    }

    @Test
    void argumentConstructor(){
        SoulmateEnemyRequest soulmateEnemy = new SoulmateEnemyRequest("user1");
        assertEquals("user1", soulmateEnemy.getUsername());
    }
}
