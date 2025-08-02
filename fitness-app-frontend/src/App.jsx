import { Box, Button, Typography } from "@mui/material";
import { useContext, useEffect, useState } from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { useDispatch } from "react-redux";
import { BrowserRouter as Router, Navigate, Route, Routes } from "react-router";
import { setCredentials } from "./store/authSlice";
import ActivityForm from "./components/ActivityForm";
import ActivityList from "./components/ActivityList";
import ActivityDetail from "./components/ActivityDetail";

const ActvitiesPage = () => {
  return (
    <Box sx={{ 
      p: 3, 
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)',
      fontFamily: '"Roboto", sans-serif',
    }}>
      <ActivityForm onActivitiesAdded={() => window.location.reload()} />
      <ActivityList />
    </Box>
  );
}

function App() {
  const { token, tokenData, logIn } = useContext(AuthContext);
  const dispatch = useDispatch();
  const [authReady, setAuthReady] = useState(false);

  useEffect(() => {
    if (token) {
      dispatch(setCredentials({ token, user: tokenData }));
      setAuthReady(true);
    }
  }, [token, tokenData, dispatch]);

  return (
    <Router>
      {!token ? (
        <Box
          sx={{
            height: "100vh",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            textAlign: "center",
            background: 'linear-gradient(135deg, #1f2937 0%, #4b5563 50%, #6b7280 100%)',
            color: 'white',
            fontFamily: '"Roboto", sans-serif',
            position: 'relative',
            overflow: 'hidden',
            '&::before': {
              content: '""',
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              background: 'radial-gradient(circle at center, rgba(255,255,255,0.1) 0%, transparent 70%)',
              zIndex: 1,
              animation: 'wave 10s infinite ease-in-out',
            },
            '@keyframes wave': {
              '0%': { transform: 'translateY(0)' },
              '50%': { transform: 'translateY(-20px)' },
              '100%': { transform: 'translateY(0)' },
            },
          }}
        >
          <Box sx={{ zIndex: 2, maxWidth: '600px', px: 2 }}>
            <Typography 
              variant="h1" 
              gutterBottom 
              sx={{ 
                fontWeight: 'bold', 
                fontFamily: '"Oswald", sans-serif',
                textShadow: '0 6px 15px rgba(0,0,0,0.4)',
                letterSpacing: '1px',
                mb: 3,
                fontSize: '3.5rem',
                opacity: 0,
                animation: 'fadeIn 1.5s ease-in-out forwards',
                '@keyframes fadeIn': {
                  '0%': { opacity: 0, transform: 'translateY(-20px)' },
                  '100%': { opacity: 1, transform: 'translateY(0)' },
                },
              }}
            >
              Fitness Tracker AI
            </Typography>
            <Typography 
              variant="h5" 
              sx={{ 
                mb: 4, 
                fontFamily: '"Oswald", sans-serif',
                fontWeight: 300,
                color: 'rgba(255,255,255,0.9)',
                lineHeight: 1.5,
                opacity: 0,
                animation: 'fadeIn 2s ease-in-out forwards 0.5s',
                '@keyframes fadeIn': {
                  '0%': { opacity: 0, transform: 'translateY(-20px)' },
                  '100%': { opacity: 1, transform: 'translateY(0)' },
                },
              }}
            >
              Track your activities, optimize your workouts, and achieve your fitness goals with ease!
            </Typography>
            <Button
              variant="contained"
              size="large"
              onClick={() => logIn()}
              sx={{
                bgcolor: '#3b82f6',
                color: 'white',
                fontFamily: '"Roboto", sans-serif',
                fontWeight: 'bold',
                fontSize: '1.2rem',
                px: 4,
                py: 1.5,
                borderRadius: 3,
                boxShadow: '0 4px 14px rgba(0,0,0,0.3)',
                transition: 'transform 0.3s ease, box-shadow 0.3s ease',
                animation: 'pulse 2s infinite',
                '@keyframes pulse': {
                  '0%': { transform: 'scale(1)' },
                  '50%': { transform: 'scale(1.02)' },
                  '100%': { transform: 'scale(1)' },
                },
                '&:hover': {
                  bgcolor: '#2563eb',
                  transform: 'scale(1.05)',
                  boxShadow: '0 6px 20px rgba(0,0,0,0.4)',
                  animation: 'none',
                },
                '&:active': {
                  transform: 'scale(0.98)',
                },
              }}
            >
              Start Your Journey
            </Button>
          </Box>
        </Box>
      ) : (
        <Routes>
          <Route path="/activities" element={<ActvitiesPage />} />
          <Route path="/activities/:id" element={<ActivityDetail />} />
          <Route path="/" element={token ? <Navigate to="/activities" replace /> : <div>Welcome! Please Login.</div>} />
        </Routes>
      )}
    </Router>
  )
}

export default App;