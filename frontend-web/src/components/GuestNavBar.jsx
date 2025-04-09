import logo from "../assets/logo.png";
import React from "react";
import { Link } from "react-router-dom";

export default function GuestNavBar() {

  const colors = {
    yellow: "#F0B542",
    darkBlue: "#042C3C",
    coral: "#EA6C7B",
    cream: "#FFF7EC",
  }

  return (
    <div
      style={{
        position: "relative",
        zIndex: 10,
        backgroundColor: "transparent",
        padding: "20px 0",
      }}
    >
      <div
        style={{
          maxWidth: "1200px",
          margin: "0 auto",
          padding: "0 2px",
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        {/* Logo and Name */}
        <div style={{ display: "flex", alignItems: "center" }}>
          <img
            src={logo}
            alt="FurrEverCare Logo"
            style={{
              height: "100px",
              width: "130px",
              objectFit: "contain",
              marginRight: "10px",
              marginLeft: "-250px"
            }}
          />
          <span
            style={{  

              fontWeight: "bold",
              color: colors.darkBlue,
              fontFamily: "'Baloo 2', cursive",
              fontSize: "30px",
            }}
          >
            FurrEverCare
          </span>
        </div>

        {/* Navigation Links */}
        <div style={{ display: "flex", alignItems: "center", padding: "30px"}}>
          <Link
            to="/"
            className="nav-link"
            style={{
              fontWeight: "bold",
              color: colors.darkBlue,
              marginRight: "120px",
              textDecoration: "none",
              fontSize: "23px",
              fontFamily: "'Baloo 2', cursive",
            }}
          >
            PawPedia
          </Link>

          <Link
            to="/our-app"
            className="nav-link"
            style={{
              fontWeight: "bold",
              color: colors.darkBlue,
              marginRight: "120px",
              textDecoration: "none",
              fontSize: "23px",
              fontFamily: "'Baloo 2', cursive",
            }}
          >
            Our App
          </Link>

          <Link
            to="/about-us"
            className="nav-link"
            style={{
              fontWeight: "bold",
              color: colors.darkBlue,
              marginRight: "400px",
              textDecoration: "none",
              fontSize: "23px",
              fontFamily: "'Baloo 2', cursive",
            }}
          >
            About Us
          </Link>

          <button
            style={{
              border: `3px solid ${colors.coral}`,
              color: colors.coral,
              borderRadius: "9999px",
              padding: "3px 70px",
              backgroundColor: "transparent",
              fontWeight: "bold",
              fontSize: "20px",
              cursor: "pointer",
              textTransform: "none",
              fontFamily: "'Baloo 2', cursive",
              marginRight: "-270px"
            }}
          >
            Login
          </button>
        </div>
      </div>
    </div>
  );
}
