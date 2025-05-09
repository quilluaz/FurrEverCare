import React, { useEffect } from 'react';
import {
  BrowserRouter as Router,
  Route,
  Routes,
  useNavigate,
  useLocation,
} from 'react-router-dom';
import { onAuthStateChanged } from 'firebase/auth';
import { auth } from './config/firebase-config';
import AuthService from './config/AuthService';

// Screens
import PawPedia from './screens/PawPedia';
import AboutUs from './screens/AboutUs';
import LogInModal from './components/LogInModal';
import WellnessTimeline from './screens/WellnessTimeline';
import UserNavBar from './components/UserNavBar';
import SignUpPage from './screens/SignUpPage';
import PetProfiles from './screens/PetProfiles';
import UserPawPedia from './screens/UserPawPedia';
import UserProfile from './screens/UserProfile';
import TreatmentPlans from './screens/TreatmentPlans';
import Tasks from './screens/Tasks';

const colors = {
  yellow: '#F0B542',
  darkBlue: '#042C3C',
  coral: '#EA6C7B',
  cream: '#FFF7EC',
};

const AppContent = () => {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    AuthService.init(); // Initialize AuthService (sets up Axios interceptors)

    const unsubscribe = onAuthStateChanged(auth, (user) => {
      console.log('App: Auth state changed:', user ? 'User present' : 'No user');

      const protectedRoutes = [
        '/mypets',
        '/wellness-timeline',
        '/home-pawpedia',
        '/profile',
        '/treat',
        '/tasks',
      ];

      const publicRoutes = ['/', '/about-us', '/login', '/signup'];
      const currentPath = location.pathname;

      if (user && AuthService.isAuthenticated()) {
        console.log('App: User authenticated');
        if (protectedRoutes.includes(currentPath) || publicRoutes.includes(currentPath)) {
          console.log('App: Staying on current route:', currentPath);
          // Stay where the user is
        } else {
          console.log('App: Unknown route for authenticated user, redirecting to /mypets');
          navigate('/mypets');
        }
      } else {
        console.log('App: User not authenticated, handling redirect');
        AuthService.clearAuth(); // Clear JWT and user data if no Firebase user

        if (protectedRoutes.includes(currentPath)) {
          console.log('App: Unauthenticated user on protected route, redirecting to /login');
          navigate('/login', { state: { from: currentPath } }); // Pass intended route for post-login redirect
        } else if (!publicRoutes.includes(currentPath)) {
          console.log('App: Unauthenticated user on unknown route, redirecting to /');
          navigate('/');
        } else {
          console.log('App: Unauthenticated user on public route, no redirect needed');
        }
      }
    });

    return () => {
      console.log('App: Cleaning up auth state listener');
      unsubscribe();
    };
  }, [navigate, location]);

  return (
    <div style={{ overflowX: 'hidden' }}>
      <Routes>
        <Route path="/" element={<PawPedia />} />
        <Route path="/about-us" element={<AboutUs />} />
        <Route path="/login" element={<LogInModal />} />
        <Route path="/signup" element={<SignUpPage />} />
        <Route path="/wellness-timeline" element={<WellnessTimeline />} />
        <Route path="/navbar" element={<UserNavBar />} />
        <Route path="/mypets" element={<PetProfiles />} />
        <Route path="/home-pawpedia" element={<UserPawPedia />} />
        <Route path="/profile" element={<UserProfile />} />
        <Route path="/treat" element={<TreatmentPlans />} />
        <Route path="/tasks" element={<Tasks />} />
      </Routes>
    </div>
  );
};

function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

export default App;
