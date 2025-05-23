import logo from "../assets/logo.png";
import { Link, useLocation, useNavigate } from "react-router-dom";
import AuthService from "../config/AuthService";
import { useEffect, useState } from "react";

function UserNavBar() {
  const navigate = useNavigate();
  const location = useLocation();
  const [userName, setUserName] = useState("");

  useEffect(() => {
    const user = AuthService.getUser();
    if (user && user.name) {
      setUserName(user.name);
    }
  }, []);

  const handleProfileClick = () => {
    navigate("/profile");
  };

  const isActive = (path) => location.pathname === path;

  const firstLetter = userName ? userName.charAt(0).toUpperCase() : "";

  return (
    <nav
      className="bg-white py-4 font-['Baloo'] border-t-[px] border-b-[3px]"
      style={{ borderColor: "#8A973F" }}
    >
      <div className="flex items-center justify-between px-6">
        {/* Logo + Name */}
        <div className="flex items-center gap-3 flex-shrink-0">
          <img
            src={logo || "/placeholder.svg"}
            alt="FurrEverCare Logo"
            className="h-[72px] object-contain md:h-[60px]"
          />
          <span
            className="font-bold text-xl md:text-2xl lg:text-2xl"
            style={{ color: "#042C3C", fontFamily: "'Baloo'" }}
          >
            FurrEverCare
          </span>
        </div>

        {/* Nav Links */}
        <div className="flex gap-6 text-sm md:text-base lg:text-lg font-['Baloo']">
          {[
            { to: "/home-pawpedia", label: "PawPedia" },
            { to: "/mypets", label: "My Pets" },
            { to: "/treat", label: "Treatment Plans" },
            { to: "/tasks", label: "Tasks" }, 
            { to: "/wellness-timeline", label: "Wellness Timeline" },
            // Uncommented Tasks link
          ].map(({ to, label }) => (
            <Link
              key={to}
              to={to}
              className={`relative px-2 md:px-4 py-2 transition-colors duration-200 hover:text-[#EA6C7B]`}
              style={{
                color: isActive(to) ? "#EA6C7B" : "#042C3C",
                fontFamily: "'Baloo'",
                display: "inline-flex",
                alignItems: "center",
              }}
            >
              {label}
              {isActive(to) && (
                <span className="absolute bottom-0 left-0 w-full h-1 bg-[#EA6C7B] rounded-t-md"></span>
              )}
            </Link>
          ))}
        </div>

        {/* Profile Button */}
        <div className="flex-shrink-0">
          <button
            onClick={handleProfileClick}
            className={`rounded-full bg-[#042C3C] hover:bg-[#042C3C]/80 transition-opacity ${
              isActive("/profile") ? "ring-2 ring-[#EA6C7B]" : ""
            }`}
            style={{
              width: "40px",
              height: "40px",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              color: "white",
              cursor: "pointer",
              fontFamily: "'Baloo'",
              fontSize: "18px",
              fontWeight: "bold",
            }}
          >
            {firstLetter || "U"}
          </button>
        </div>
      </div>
    </nav>
  );
}

export default UserNavBar;