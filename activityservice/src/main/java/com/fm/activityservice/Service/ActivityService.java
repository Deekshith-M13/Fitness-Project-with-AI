package com.fm.activityservice.Service;

import com.fm.activityservice.Model.Activity;
import com.fm.activityservice.Repository.ActivityRepository;
import com.fm.activityservice.dto.ActivityRequest;
import com.fm.activityservice.dto.ActivityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserValidationService userValidationService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchange;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;



    public ActivityResponse addActivity(ActivityRequest request) {

        boolean isValid = userValidationService.validateUserId(request.getUserId());
        if(!isValid) {
            throw new RuntimeException("Invalid userId");
        }
        Activity newactivity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .caloriesBurned(request.getCaloriesBurned())
                .duration(request.getDuration())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        Activity savedActivity = activityRepository.save(newactivity);

//        Publish to RabbitMq
        try{
            rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
        }catch(Exception e){
            log.error("Failed to publish activity to RabbitMq" + e);
        }

        return toActivityResponse(savedActivity);
    }

    public ActivityResponse toActivityResponse(Activity activity) {
        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setId(activity.getId());
        activityResponse.setUserId(activity.getUserId());
        activityResponse.setType(activity.getType());
        activityResponse.setCaloriesBurned(activity.getCaloriesBurned());
        activityResponse.setDuration(activity.getDuration());
        activityResponse.setStartTime(activity.getStartTime());
        activityResponse.setAdditionalMetrics(activity.getAdditionalMetrics());
        activityResponse.setCreatedDate(activity.getCreatedDate());
        activityResponse.setUpdatedDate(activity.getUpdatedDate());
        return activityResponse;
    }

    public List<ActivityResponse> getActivities(String userid) {
       List<Activity> activities =  activityRepository.findByUserId(userid);

       return activities.stream()
               .map(this::toActivityResponse)
               .collect(Collectors.toList());
    }

    public ActivityResponse getActivity(String id) {
        return activityRepository.findById(id)
                .map(this::toActivityResponse)
                .orElseThrow(() ->  new RuntimeException("Activity not found"));
    }
}
