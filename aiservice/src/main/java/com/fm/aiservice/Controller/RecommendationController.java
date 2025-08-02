package com.fm.aiservice.Controller;

import com.fm.aiservice.Model.Recommendation;
import com.fm.aiservice.Repository.RecommendationRepository;
import com.fm.aiservice.Service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recommendation>> getUserRecommendations(@PathVariable String userId) {

        return ResponseEntity.ok(recommendationService.getUserRecommendation(userId));

    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Recommendation> getActivityRecommendations(@PathVariable String activityId) {

        return ResponseEntity.ok(recommendationService.getActivityRecommendation(activityId));
    }
}
