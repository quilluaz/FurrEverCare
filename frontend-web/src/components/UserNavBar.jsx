"use client"
import logo from "../assets/logo.png"
import { Link, useLocation, useNavigate } from "react-router-dom"

function UserNavBar() {
  const navigate = useNavigate()
  const location = useLocation() // Get current location to determine active link

  const handleProfileClick = () => {
    navigate("/profile")
  }

  // Function to determine if a link is active
  const isActive = (path) => {
    return location.pathname === path
  }

  return (
    <nav
      className="bg-white py-4 font-['Baloo']"
      style={{ borderTop: "4px solid #8A973F", borderBottom: "4px solid #8A973F" }}
    >
      <div className="container mx-auto flex items-center justify-between">
        <div className="flex items-center justify-start">
          <img src={logo || "/placeholder.svg"} alt="FurrEverCare Logo" className="h-18 -ml-35" />
          <span className="font-bold text-xl" style={{ color: "#042C3C", fontSize: "1.5rem", padding: "10px" }}>
            FurrEverCare
          </span>
        </div>

        <div className="flex space-x-8" style={{ fontSize: "1.5rem" }}>
          <Link
            to="/home-pawpedia"
            style={{ color: isActive("/home-pawpedia") ? "#EA6C7B" : "#042C3C" }}
            className="relative px-4 py-2 transition-colors duration-200 hover:text-[#EA6C7B]"
          >
            PawPedia
            {isActive("/home-pawpedia") && (
              <span className="absolute bottom-0 left-0 w-full h-1 bg-[#EA6C7B] rounded-t-md"></span>
            )}
          </Link>

          <Link
            to="/mypets"
            style={{ color: isActive("/mypets") ? "#EA6C7B" : "#042C3C" }}
            className="relative px-4 py-2 transition-colors duration-200 hover:text-[#EA6C7B]"
          >
            My Pets
            {isActive("/mypets") && (
              <span className="absolute bottom-0 left-0 w-full h-1 bg-[#EA6C7B] rounded-t-md"></span>
            )}
          </Link>

          <Link
            to="/tracker"
            style={{ color: isActive("/tracker") ? "#EA6C7B" : "#042C3C" }}
            className="relative px-4 py-2 transition-colors duration-200 hover:text-[#EA6C7B]"
          >
            Treatment Tracker
            {isActive("/tracker") && (
              <span className="absolute bottom-0 left-0 w-full h-1 bg-[#EA6C7B] rounded-t-md"></span>
            )}
          </Link>

          <Link
            to="/wellness-timeline"
            style={{ color: isActive("/wellness-timeline") ? "#EA6C7B" : "#042C3C" }}
            className="relative px-4 py-2 transition-colors duration-200 hover:text-[#EA6C7B]"
          >
            Wellness Timeline
            {isActive("/wellness-timeline") && (
              <span className="absolute bottom-0 left-0 w-full h-1 bg-[#EA6C7B] rounded-t-md"></span>
            )}
          </Link>
        </div>

        <div>
          <button
            onClick={handleProfileClick}
            className={`rounded-full bg-[#042C3C] hover:bg-[#042C3C]/80 transition-opacity ${isActive("/profile") ? "ring-2 ring-[#EA6C7B]" : ""}`}
            style={{
              width: "40px",
              height: "40px",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              color: "white",
              cursor: "pointer",
            }}
          >
            <i className="fas fa-user-circle"></i>
          </button>
        </div>
      </div>
    </nav>
  )
}

export default UserNavBar
