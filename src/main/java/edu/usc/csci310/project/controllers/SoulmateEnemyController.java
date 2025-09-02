package edu.usc.csci310.project.controllers;

import edu.usc.csci310.project.models.Song;
import edu.usc.csci310.project.requests.SoulmateEnemyRequest;
import edu.usc.csci310.project.responses.SoulmateEnemyResponse;
import edu.usc.csci310.project.services.SoulmateEnemyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class SoulmateEnemyController {
    private final SoulmateEnemyService seService;

    public SoulmateEnemyController(SoulmateEnemyService seService) {
        this.seService = seService;
    }

    @PostMapping("/getSoulmate")
    public ResponseEntity<SoulmateEnemyResponse> getSoulmateForUser(@RequestBody SoulmateEnemyRequest request) {
        try {
            if (request.getUsername() == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .header("X-Error-Message", "Null username.")
                        .body(null);
            }

            String soulmate = seService.getSoulmateForUser(request.getUsername());
            List<Song> favorites = seService.getFavoriteSongs(soulmate);
            SoulmateEnemyResponse response = new SoulmateEnemyResponse(soulmate, favorites);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Error finding user: " + e.getMessage())
                    .body(null);
        }
    }

    @PostMapping("/getEnemy")
    public ResponseEntity<SoulmateEnemyResponse> getEnemyForUser(@RequestBody SoulmateEnemyRequest request) {
        try {
            if (request.getUsername() == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .header("X-Error-Message", "Null username.")
                        .body(null);
            }

            String enemy = seService.getEnemyForUser(request.getUsername());
            List<Song> favorites = seService.getFavoriteSongs(enemy);
            SoulmateEnemyResponse response = new SoulmateEnemyResponse(enemy, favorites);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Error finding user: " + e.getMessage())
                    .body(null);
        }
    }
}
