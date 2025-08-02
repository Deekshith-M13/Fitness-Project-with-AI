import { Card, CardContent, Grid, Typography } from '@mui/material';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getActivities } from '../services/api';

const ActivityList = () => {
  const [activities, setActivities] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchActivities = async () => {
      try {
        const response = await getActivities();
        setActivities(response.data);
      } catch (error) {
        console.error(error);
      }
    };

    fetchActivities();
  }, []);

  return (
    <Grid container spacing={3} sx={{ mt: 2, fontFamily: '"Ubuntu", sans-serif' }}>
      {activities.map((activity) => (
        <Grid item xs={12} sm={6} md={4} key={activity.id}>
          <Card
            sx={{
              cursor: 'pointer',
              transition: 'transform 0.2s ease',
              '&:hover': {
                transform: 'scale(1.05)',
                backgroundColor: '#e0f7fa'
              }
            }}
            onClick={() => navigate(`/activities/${activity.id}`)}
          >
            <CardContent>
              <Typography variant="h6" color="primary" sx={{ fontFamily: '"Ubuntu", sans-serif' }}>{activity.type}</Typography>
              <Typography sx={{ fontFamily: '"Ubuntu", sans-serif' }}>Duration: {activity.duration} mins</Typography>
              <Typography sx={{ fontFamily: '"Ubuntu", sans-serif' }}>Calories: {activity.caloriesBurned}</Typography>
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
};

export default ActivityList;