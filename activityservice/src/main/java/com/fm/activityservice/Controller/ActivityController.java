package com.fm.activityservice.Controller;


import com.fm.activityservice.Service.ActivityService;
import com.fm.activityservice.dto.ActivityRequest;
import com.fm.activityservice.dto.ActivityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponse> addActivity(@RequestBody ActivityRequest activity,@RequestHeader("X-User-ID") String userId) {
        if(userId !=null){
            activity.setUserId(userId);
        }
        return ResponseEntity.ok(activityService.addActivity(activity));
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>>  getActivities(@RequestHeader("X-User-ID") String userid) {

        return ResponseEntity.ok(activityService.getActivities(userid));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponse>  getActivity(@PathVariable String id) {

        return ResponseEntity.ok(activityService.getActivity(id));
    }

}
