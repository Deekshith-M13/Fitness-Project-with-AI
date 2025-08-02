package com.fm.aiservice.Service;

import com.fm.aiservice.Model.Activity;
import com.fm.aiservice.Model.Recommendation;
import com.fm.aiservice.Repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAiService activityAiService;
    private final RecommendationRepository recommendationRepository;

    @RabbitListener(queues = "activity.queue")
    public void processedActivity(Activity activity) {
        log.info("Processing activity {}", activity.getId());
//      log.info("Generated Recommendation {}", activityAiService.generateRecommendation(activity) );
        Recommendation recommendation = activityAiService.generateRecommendation(activity);
        recommendationRepository.save(recommendation);
    }
}
