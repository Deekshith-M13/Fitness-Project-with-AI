import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router';
import { getActivityDetail } from '../services/api';
import {
  Box,
  Card,
  CardContent,
  Divider,
  Typography,
  Chip,
  Grid,
  Button,
} from '@mui/material';

const ActivityDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [activity, setActivity] = useState(null);
  const [recommendation, setRecommendation] = useState(null);

  const handleBack = () => {
    navigate(-1); // Goes back to previous page
  };

  useEffect(() => {
    const fetchActivityDetail = async () => {
      try {
        const response = await getActivityDetail(id);
        setActivity(response.data);
        setRecommendation(response.data.recommendation);
      } catch (error) {
        console.error(error);
      }
    };

    fetchActivityDetail();
  }, [id]);

  if (!activity) {
    return <Typography sx={{ fontFamily: '"Roboto", sans-serif' }}>Loading...</Typography>;
  }

  return (
    <Box sx={{ 
      maxWidth: 1000, 
      mx: 'auto', 
      p: 3,
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)',
      fontFamily: '"Roboto", sans-serif',
    }}>
      {/* Back Button */}
      <Box sx={{ mb: 3 }}>
        <Button
          onClick={handleBack}
          sx={{
            color: '#e2e8f0',
            bgcolor: 'rgba(255,255,255,0.1)',
            border: '1px solid rgba(255,255,255,0.2)',
            borderRadius: 2,
            px: 3,
            py: 1,
            fontWeight: 'bold',
            fontFamily: '"Roboto", sans-serif',
            '&:hover': {
              bgcolor: 'rgba(255,255,255,0.2)',
              borderColor: 'rgba(255,255,255,0.3)',
            }
          }}
        >
          ← Back to Activities
        </Button>
      </Box>

      {/* Activity Info Card */}
      <Card sx={{ 
        mb: 4, 
        boxShadow: '0 20px 40px rgba(0,0,0,0.2)', 
        borderRadius: 4,
        background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
        color: 'white',
        border: '1px solid rgba(255,255,255,0.1)'
      }}>
        <CardContent sx={{ p: 4 }}>
          <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', mb: 3, color: '#e2e8f0', fontFamily: '"Roboto", sans-serif' }}>
            Activity Details
          </Typography>
          <Grid container spacing={3}>
            <Grid item xs={12} sm={6}>
              <Box sx={{ 
                p: 3, 
                borderRadius: 3, 
                bgcolor: 'rgba(255,255,255,0.05)', 
                backdropFilter: 'blur(10px)',
                border: '1px solid rgba(255,255,255,0.1)'
              }}>
                <Typography variant="h6" sx={{ mb: 1, fontWeight: 'bold', color: '#94a3b8', fontFamily: '"Roboto", sans-serif' }}>
                  Activity Type
                </Typography>
                <Chip 
                  label={activity.type} 
                  sx={{ 
                    bgcolor: '#059669', 
                    color: 'white', 
                    fontWeight: 'bold',
                    fontSize: '1rem',
                    px: 2,
                    fontFamily: '"Roboto", sans-serif'
                  }} 
                />
              </Box>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Box sx={{ 
                p: 3, 
                borderRadius: 3, 
                bgcolor: 'rgba(255,255,255,0.05)', 
                backdropFilter: 'blur(10px)',
                border: '1px solid rgba(255,255,255,0.1)'
              }}>
                <Typography variant="h6" sx={{ mb: 1, fontWeight: 'bold', color: '#94a3b8', fontFamily: '"Roboto", sans-serif' }}>
                  Date & Time
                </Typography>
                <Typography variant="body1" sx={{ fontWeight: 'bold', color: '#e2e8f0', fontFamily: '"Roboto", sans-serif' }}>
                  {new Date(activity.createdAt).toLocaleString()}
                </Typography>
              </Box>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Recommendation Card */}
      {recommendation && (
        <Card sx={{ 
          boxShadow: '0 20px 40px rgba(0,0,0,0.2)', 
          borderRadius: 4, 
          background: 'linear-gradient(135deg, #111827 0%, #1f2937 100%)',
          color: 'white',
          border: '1px solid rgba(255,255,255,0.1)'
        }}>
          <CardContent sx={{ p: 4 }}>
            <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', mb: 3, color: '#e2e8f0', fontFamily: '"Roboto", sans-serif' }}>
              AI Recommendation
            </Typography>

            {/* Analysis */}
            <Box sx={{ 
              mb: 4, 
              p: 3, 
              borderRadius: 3, 
              bgcolor: 'rgba(255,255,255,0.05)', 
              backdropFilter: 'blur(10px)',
              border: '1px solid rgba(255,255,255,0.1)'
            }}>
              <Typography variant="h5" sx={{ mb: 2, fontWeight: 'bold', color: '#fbbf24', fontFamily: '"Roboto", sans-serif' }}>
                Analysis
              </Typography>
              <Typography variant="body1" sx={{ fontSize: '1.1rem', lineHeight: 1.6, color: '#d1d5db', fontFamily: '"Roboto", sans-serif' }}>
                {activity.recommendation}
              </Typography>
            </Box>

            {/* Improvements */}
            {activity.improvements && activity.improvements.length > 0 && (
              <Box sx={{ 
                mb: 4, 
                p: 3, 
                borderRadius: 3, 
                bgcolor: 'rgba(255,255,255,0.05)', 
                backdropFilter: 'blur(10px)',
                border: '1px solid rgba(255,255,255,0.1)'
              }}>
                <Typography variant="h5" sx={{ mb: 3, fontWeight: 'bold', color: '#10b981', fontFamily: '"Roboto", sans-serif' }}>
                  Improvements
                </Typography>
                {activity.improvements
                  .filter(item => item && item.trim() !== '')
                  .map((item, index) => (
                    <Box key={index} sx={{ 
                      display: 'flex', 
                      alignItems: 'flex-start', 
                      mb: 2,
                      p: 2,
                      borderRadius: 2,
                      bgcolor: 'rgba(255,255,255,0.03)',
                      border: '1px solid rgba(255,255,255,0.05)'
                    }}>
                      <Box sx={{ 
                        width: 6, 
                        height: 6, 
                        borderRadius: '50%', 
                        bgcolor: '#10b981', 
                        mr: 2, 
                        mt: 1,
                        flexShrink: 0 
                      }} />
                      <Typography variant="body1" sx={{ fontSize: '1rem', lineHeight: 1.5, color: '#d1d5db', fontFamily: '"Roboto", sans-serif' }}>
                        {item}
                      </Typography>
                    </Box>
                  ))}
              </Box>
            )}

            {/* Suggestions */}
            {activity.suggestions && activity.suggestions.length > 0 && (
              <Box sx={{ 
                mb: 4, 
                p: 3, 
                borderRadius: 3, 
                bgcolor: 'rgba(255,255,255,0.05)', 
                backdropFilter: 'blur(10px)',
                border: '1px solid rgba(255,255,255,0.1)'
              }}>
                <Typography variant="h5" sx={{ mb: 3, fontWeight: 'bold', color: '#3b82f6', fontFamily: '"Roboto", sans-serif' }}>
                  Suggestions
                </Typography>
                {activity.suggestions
                  .filter(item => item && item.trim() !== '')
                  .map((item, index) => (
                    <Box key={index} sx={{ 
                      display: 'flex', 
                      alignItems: 'flex-start', 
                      mb: 2,
                      p: 2,
                      borderRadius: 2,
                      bgcolor: 'rgba(255,255,255,0.03)',
                      border: '1px solid rgba(255,255,255,0.05)'
                    }}>
                      <Box sx={{ 
                        width: 6, 
                        height: 6, 
                        borderRadius: '50%', 
                        bgcolor: '#3b82f6', 
                        mr: 2, 
                        mt: 1,
                        flexShrink: 0 
                      }} />
                      <Typography variant="body1" sx={{ fontSize: '1rem', lineHeight: 1.5, color: '#d1d5db', fontFamily: '"Roboto", sans-serif' }}>
                        {item}
                      </Typography>
                    </Box>
                  ))}
              </Box>
            )}

            {/* Nutrition and Hydration Tips */}
            {activity.safety && activity.safety.length > 0 && (
              <Box sx={{ 
                p: 3, 
                borderRadius: 3, 
                bgcolor: 'rgba(255,255,255,0.05)', 
                backdropFilter: 'blur(10px)',
                border: '1px solid rgba(255,255,255,0.1)'
              }}>
                <Typography variant="h5" sx={{ mb: 3, fontWeight: 'bold', color: '#f59e0b', fontFamily: '"Roboto", sans-serif' }}>
                  Nutrition and Hydration Tips
                </Typography>
                {activity.safety
                  .filter(item => item && item.trim() !== '')
                  .map((item, index) => (
                    <Box key={index} sx={{ 
                      display: 'flex', 
                      alignItems: 'flex-start', 
                      mb: 2,
                      p: 2,
                      borderRadius: 2,
                      bgcolor: 'rgba(255,255,255,0.03)',
                      border: '1px solid rgba(255,255,255,0.05)'
                    }}>
                      <Box sx={{ 
                        width: 6, 
                        height: 6, 
                        borderRadius: '50%', 
                        bgcolor: '#f59e0b', 
                        mr: 2, 
                        mt: 1,
                        flexShrink: 0 
                      }} />
                      <Typography variant="body1" sx={{ fontSize: '1rem', lineHeight: 1.5, color: '#d1d5db', fontFamily: '"Roboto", sans-serif' }}>
                        {item}
                      </Typography>
                    </Box>
                  ))}
              </Box>
            )}
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default ActivityDetail;