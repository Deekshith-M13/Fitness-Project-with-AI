import {
  Box,
  Button,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  TextField,
  Paper,
  Typography
} from '@mui/material';
import React, { useState, useContext } from 'react';
import { AuthContext } from "react-oauth2-code-pkce";
import { addActivity } from '../services/api';

const activityTypes = [
  "RUNNING", "WALKING", "CYCLING", "YOGA",
  "GYM", "CARDIO", "SWIMMING", "OTHER"
];

const ActivityForm = ({ onActivityAdded }) => {
  const { logOut } = useContext(AuthContext);
  const [activity, setActivity] = useState({
    type: "RUNNING", duration: '', caloriesBurned: ''
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await addActivity(activity);
      setActivity({ type: "RUNNING", duration: '', caloriesBurned: '' });
      window.location.reload(); // ✅ Force reload to refresh ActivityList
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <Box sx={{ fontFamily: '"Roboto", sans-serif' }}>
      <Box sx={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        mb: 3,
        p: 2,
        borderRadius: 2,
        bgcolor: 'rgba(255,255,255,0.05)',
        border: '1px solid rgba(255,255,255,0.1)'
      }}>
        <Typography variant="h4" sx={{ color: '#e2e8f0', fontWeight: 'bold', fontFamily: '"Roboto", sans-serif' }}>
          Fitness Tracker
        </Typography>
        <Button
          variant="contained"
          onClick={logOut}
          sx={{
            bgcolor: '#dc2626',
            color: 'white',
            fontWeight: 'bold',
            px: 3,
            fontFamily: '"Roboto", sans-serif',
            '&:hover': {
              bgcolor: '#b91c1c',
            }
          }}
        >
          Logout
        </Button>
      </Box>

      <Paper elevation={4} sx={{
        p: 4,
        mb: 4,
        borderRadius: 3,
        background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
        border: '1px solid rgba(255,255,255,0.1)',
        color: 'white'
      }}>
        <Typography variant="h5" gutterBottom fontWeight="bold" sx={{ color: '#e2e8f0', mb: 3, fontFamily: '"Roboto", sans-serif' }}>
          Add New Activity
        </Typography>
        <Box component="form" onSubmit={handleSubmit}>
          <FormControl fullWidth sx={{ mb: 2 }}>
            <InputLabel sx={{ color: '#94a3b8', fontFamily: '"Roboto", sans-serif' }}>Activity Type</InputLabel>
            <Select
              value={activity.type}
              label="Activity Type"
              onChange={(e) => setActivity({ ...activity, type: e.target.value })}
              sx={{
                color: 'white',
                fontFamily: '"Roboto", sans-serif',
                '& .MuiOutlinedInput-notchedOutline': {
                  borderColor: 'rgba(255,255,255,0.2)',
                },
                '&:hover .MuiOutlinedInput-notchedOutline': {
                  borderColor: 'rgba(255,255,255,0.3)',
                },
                '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                  borderColor: '#3b82f6',
                }
              }}
            >
              {activityTypes.map((type) => (
                <MenuItem key={type} value={type} sx={{ fontFamily: '"Roboto", sans-serif' }}>{type}</MenuItem>
              ))}
            </Select>
          </FormControl>

          <TextField
            fullWidth
            label="Duration (Minutes)"
            type="number"
            sx={{
              mb: 2,
              '& .MuiInputLabel-root': { color: '#94a3b8', fontFamily: '"Roboto", sans-serif' },
              '& .MuiOutlinedInput-root': {
                color: 'white',
                fontFamily: '"Roboto", sans-serif',
                '& fieldset': {
                  borderColor: 'rgba(255,255,255,0.2)',
                },
                '&:hover fieldset': {
                  borderColor: 'rgba(255,255,255,0.3)',
                },
                '&.Mui-focused fieldset': {
                  borderColor: '#3b82f6',
                },
              }
            }}
            value={activity.duration}
            onChange={(e) => setActivity({ ...activity, duration: e.target.value })}
          />

          <TextField
            fullWidth
            label="Calories Burned"
            type="number"
            sx={{
              mb: 3,
              '& .MuiInputLabel-root': { color: '#94a3b8', fontFamily: '"Roboto", sans-serif' },
              '& .MuiOutlinedInput-root': {
                color: 'white',
                fontFamily: '"Roboto", sans-serif',
                '& fieldset': {
                  borderColor: 'rgba(255,255,255,0.2)',
                },
                '&:hover fieldset': {
                  borderColor: 'rgba(255,255,255,0.3)',
                },
                '&.Mui-focused fieldset': {
                  borderColor: '#3b82f6',
                },
              }
            }}
            value={activity.caloriesBurned}
            onChange={(e) => setActivity({ ...activity, caloriesBurned: e.target.value })}
          />

          <Button
            variant="contained"
            fullWidth
            type="submit"
            sx={{
              bgcolor: '#3b82f6',
              color: 'white',
              fontWeight: 'bold',
              py: 1.5,
              fontSize: '1.1rem',
              fontFamily: '"Roboto", sans-serif',
              '&:hover': {
                bgcolor: '#2563eb',
              }
            }}
          >
            Add Activity
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default ActivityForm;
