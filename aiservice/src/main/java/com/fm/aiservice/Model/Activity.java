package com.fm.aiservice.Model;



import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Activity {
    private String id;
    private String userId;
    private int duration;
    @JsonProperty("type")
    private String activityType;
    private int caloriesBurned;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}
