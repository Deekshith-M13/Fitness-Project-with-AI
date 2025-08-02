package com.fm.activityservice.Repository;

import com.fm.activityservice.Model.Activity;
import com.fm.activityservice.dto.ActivityRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public  interface ActivityRepository extends MongoRepository<Activity,String> {


    List<Activity> findByUserId(String userid);
}
