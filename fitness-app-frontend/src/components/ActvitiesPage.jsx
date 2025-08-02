import { Box, Typography } from "@mui/material";
import React, { useEffect, useState } from "react";
import ActivityForm from "./components/ActivityForm";
import ActivityList from "./components/ActivityList";
import { getActivities } from "../services/api";

const ActivitiesPage = () => {
  const [activities, setActivities] = useState([]);

  useEffect(() => {
    const fetchActivities = async () => {
      try {
        const response = await getActivities();
        console.log("Fetched activities:", response.data);
        if (Array.isArray(response.data)) {
          setActivities(response.data.sort((a, b) => (b.id || 0) - (a.id || 0))); // Sort newest first
        } else {
          console.warn("Response data is not an array:", response.data);
          setActivities([]);
        }
      } catch (error) {
        console.error("Fetch error:", error);
        setActivities([]);
      }
    };
    fetchActivities();
  }, []);

  const handleActivityAdded = async () => {
    try {
      const response = await getActivities();
      console.log("Refetched activities:", response.data);
      if (Array.isArray(response.data)) {
        setActivities(response.data.sort((a, b) => (b.id || 0) - (a.id || 0))); // Sort newest first
      } else {
        console.warn("Refetch data is not an array:", response.data);
        setActivities([]);
      }
    } catch (error) {
      console.error("Refetch error:", error);
      setActivities([]);
    }
  };

  return (
    <Box sx={{ 
      p: 3, 
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)',
      fontFamily: '"Roboto", sans-serif',
    }}>
      <ActivityForm onActivityAdded={handleActivityAdded} />
      <ActivityList activities={activities} />
      {!activities.length && (
        <Typography sx={{ fontFamily: '"Roboto", sans-serif', color: 'white', mt: 2 }}>
          Debug: No activities loaded. Check console for details.
        </Typography>
      )}
    </Box>
  );
};

export default ActivitiesPage;
