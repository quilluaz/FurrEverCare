import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom"; // Ensure Routes is imported
import GuestNavBar from "./components/GuestNavBar";

import PawPedia from "./screens/PawPedia";
import AboutUs from "./screens/AboutUs";
import TreatmentTracker from "./screens/TreatmentTracker";
import LogInModal from "./components/LogInModal";
import SignUpModal from "./components/SignUpModal";
import WellnessTimeline from "./screens/WellnessTimeline";
import UserNavBar from "./components/UserNavBar";
import SignUpPage from "./screens/SignUpPage";
import PetProfiles from "./screens/PetProfiles";
import UserPawPedia from "./screens/UserPawPedia";
import UserProfile from "./screens/UserProfile";


const colors = {
  yellow: "#F0B542",
  darkBlue: "#042C3C",
  coral: "#EA6C7B",
  cream: "#FFF7EC",
};

function App() {
  return (
    <Router>
      <div style={{ overflowX: 'hidden' }}> {/* Prevent horizontal scrolling */}
        {/* Define Routes (updated for React Router v6) */}
        <Routes>
          <Route path="/" element={<PawPedia />} />
          <Route path="/about-us" element={<AboutUs />} />
          <Route path="/tracker" element={<TreatmentTracker />} />
          <Route path="/login" element={<LogInModal />} />
          <Route path="/signup" element={<SignUpModal />} />
          <Route path="/register" element={<SignUpPage />} />
          <Route path="/wellness-timeline" element={<WellnessTimeline />} />
          <Route path="/navbar" element={<UserNavBar />} />
          <Route path="/mypets" element={<PetProfiles />} />
          <Route path="/pawpedia" element={<UserPawPedia />} />
          <Route path="/profile" element={<UserProfile />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
