package com.fm.aiservice.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fm.aiservice.Model.Activity;
import com.fm.aiservice.Model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);
        log.info("Generated prompt: {}", prompt);

        try {
            String aiResponse = geminiService.getAnswer(prompt);
            log.info("Raw AI Response: {}", aiResponse);
            return processAiResponse(activity, aiResponse);
        } catch (Exception e) {
            log.error("Failed to generate recommendation", e);
            return defaultRecommendation(activity);
        }
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // First parse the outer response structure
            JsonNode rootNode = objectMapper.readTree(aiResponse);

            // Extract the actual JSON content from the nested structure
            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            log.info("Extracted JSON content: {}", jsonContent);

            // Now parse the actual recommendation JSON
            JsonNode recommendationJson = objectMapper.readTree(jsonContent);

            // Process analysis section
            JsonNode analysisNode = recommendationJson.path("analysis");
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "summary", "Summary:");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRateAnalysis", "Heart Rate Analysis:");
            addAnalysisSection(fullAnalysis, analysisNode, "performanceInsights", "Performance Insights:");

            // Process recommendations
            List<String> improvements = new ArrayList<>();
            JsonNode recommendationsNode = recommendationJson.path("recommendations");
            if (recommendationsNode.isArray()) {
                recommendationsNode.forEach(rec -> {
                    improvements.add("Area: " + rec.path("area").asText());
                    improvements.add("\nImprovements: " + rec.path("improvements").asText());
                    improvements.add("\nTraining Tips: " + rec.path("trainingTips").asText());
                    improvements.add("\nRecovery: " + rec.path("recoveryGuidance").asText());
                    improvements.add("\n");
                });
            }

            // Process suggestions
            List<String> suggestions = new ArrayList<>();
            JsonNode suggestionNode = recommendationJson.path("suggestion");
            if (suggestionNode.isObject()) {
                suggestions.add(suggestionNode.path("workout").asText() + " - " +
                        suggestionNode.path("description").asText() + " - \n" +
                        suggestionNode.path("use").asText());
            }

            // Process nutrition
            List<String> safety = new ArrayList<>();
            JsonNode nutritionNode = recommendationJson.path("nutrition");
            if (nutritionNode.isObject()) {
                safety.add("Post Workout Meal: " + nutritionNode.path("postWorkoutMeal").asText() +
                        "\nHydration Tips: " + nutritionNode.path("hydrationTip").asText());
            }

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .type(activity.getActivityType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(!improvements.isEmpty() ? improvements :
                            Collections.singletonList("No improvements provided"))
                    .suggestions(!suggestions.isEmpty() ? suggestions :
                            Collections.singletonList("No suggestions provided"))
                    .safety(!safety.isEmpty() ? safety :
                            Collections.singletonList("No nutrition plan provided\nStay Hydrated"))
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Failed to process AI response. Error: " + e.getMessage() + "\nResponse was: " + aiResponse, e);
            return defaultRecommendation(activity);
        }
    }

    private Recommendation defaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getActivityType())
                .recommendation("No analysis provided")
                .improvements(Collections.singletonList("No improvements"))
                .suggestions(Collections.singletonList("No suggestions"))
                .safety(Collections.singletonList("No nutrition plan provided\nStay Hydrated"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode,
                                    String key, String prefix) {
        if (!analysisNode.path(key).isMissingNode()) {
            fullAnalysis.append(prefix)
                    .append("\n")
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
            You are given a fitness activity object in JSON with the following structure:
            
            {
              "id": "%s",
              "userId": "%s", 
              "duration": %d,
              "activityType": "%s",
              "caloriesBurned": %d,
              "startTime": "%s",
              "additionalMetrics": %s,
              "createdDate": "%s",
              "updatedDate": "%s"
            }
            
            Return a JSON response with the following structure:
            {
              "activityData": { ... },   // include original data as-is
              "analysis": {
                "summary": "...",
                "heartRateAnalysis": "...",
                "performanceInsights": "..."
              },
              "recommendations": [
                {
                  "area": "...",
                  "improvements": "...",
                  "trainingTips": "...",
                  "recoveryGuidance": "..."
                }
              ],
              "suggestion": {
                "workout": "...",
                "description": "...",
                "use": "..."
              },
              "nutrition": {
                "postWorkoutMeal": "...",
                "hydrationTip": "..."
              }
            }
            
            FORMATTING REQUIREMENTS:
            - Return valid JSON only
            - Wrap response in ```json``` blocks
            - Recommendations should be an array
            - All text values should be properly escaped for JSON
            """,
                activity.getId(),
                activity.getUserId(),
                activity.getDuration(),
                activity.getActivityType(),
                activity.getCaloriesBurned(),
                activity.getStartTime(),
                activity.getAdditionalMetrics(),
                activity.getCreatedDate(),
                activity.getUpdatedDate()
        );
    }
}