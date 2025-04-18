"use client"

import { useState, useEffect } from "react"
import UserNavBar from "../components/UserNavBar"
import WelcomeMessageModal from "../components/WelcomeMessageModal"
import pawpedia from "../assets/pawpedia.png"

export default function UserPawPedia() {
  // State for welcome modal
  const [showWelcomeModal, setShowWelcomeModal] = useState(false)

  // Show welcome modal when component mounts
  useEffect(() => {
    // Check if the modal has already been shown in this session
    const hasSeenWelcome = sessionStorage.getItem("seenWelcomePawPedia")
    if (!hasSeenWelcome) {
      setShowWelcomeModal(true)
      sessionStorage.setItem("seenWelcomePawPedia", "true") // Mark the modal as shown
    }
  }, [])

  // Define the colors
  const colors = {
    yellow: "#F0B542",
    darkBlue: "#042C3C",
    coral: "#EA6C7B",
    cream: "#FFF7EC",
  }

  return (
    <div
      style={{
        minHeight: "100vh",
        width: "100%",
        backgroundColor: colors.cream,
        position: "relative",
        overflow: "hidden",
        fontFamily: "'Plus Jakarta Sans', sans-serif",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <div style={{ position: "relative", zIndex: 10 }}>
        <UserNavBar />
      </div>

      {/* Welcome Modal */}
      <WelcomeMessageModal
        isOpen={showWelcomeModal}
        onClose={() => setShowWelcomeModal(false)}
        userName="" // You can pass the user's name here if available
      />

      {/* Background Circles */}
      <div
        style={{
          position: "absolute",
          top: 0,
          left: 0,
          width: "230px",
          height: "230px",
          borderRadius: "50%",
          backgroundColor: colors.yellow,
          transform: "translate(-30%, -30%)",
        }}
      ></div>

      <div
        style={{
          position: "absolute",
          top: "120px",
          left: "180px",
          width: "100px",
          height: "100px",
          borderRadius: "50%",
          backgroundColor: colors.yellow,
        }}
      ></div>

      <div
        style={{
          position: "absolute",
          bottom: 0,
          right: 0,
          width: "300px",
          height: "300px",
          borderRadius: "50%",
          backgroundColor: colors.yellow,
          transform: "translate(40%, 40%)",
        }}
      ></div>

      <div
        style={{
          position: "absolute",
          bottom: "150px",
          right: "100px",
          width: "80px",
          height: "80px",
          borderRadius: "50%",
          backgroundColor: colors.yellow,
        }}
      ></div>

      <div
        style={{
          position: "absolute",
          bottom: "300px",
          right: "200px",
          width: "60px",
          height: "60px",
          borderRadius: "50%",
          backgroundColor: colors.yellow,
        }}
      ></div>

      {/* Main Content */}
      <div
        style={{
          maxWidth: "1200px",
          margin: "0 auto",
          padding: "0 20px",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          flexGrow: 1,
        }}
      >
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            padding: "20px",
          }}
        >
          {/* Pets Image */}
          <img
            src={pawpedia || "/placeholder.svg"}
            alt="Pets lineup"
            style={{
              width: "130%",
              maxWidth: "470px",
              height: "auto",
              objectFit: "contain",
              marginTop: "0",
              marginBottom: "-20px",
              display: "block",
            }}
          />
          {/* Search Bar */}
          <div
            style={{
              display: "flex",
              alignItems: "center",
              width: "100%",
              maxWidth: "500px",
              border: "3px solid #042C3C",
              borderRadius: "9999px",
              backgroundColor: "white",
              padding: "8px 16px",
            }}
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke="#9CA3AF"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              style={{ marginRight: "8px" }}
            >
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
            <input
              type="text"
              placeholder="Search conditions, diseases or illnesses..."
              style={{
                flexGrow: 1,
                color: "rgba(0, 0, 0, 1)",
                backgroundColor: "transparent",
                outline: "none",
                border: "none",
                fontSize: "14px",
              }}
            />
            <button
              style={{
                backgroundColor: colors.coral,
                color: "white",
                fontSize: "13px",
                padding: "6px 16px",
                borderRadius: "9999px",
                border: "none",
                cursor: "pointer",
              }}
            >
              Ask AI
            </button>
          </div>
        </div>
      </div>
      {/* Font imports */}
      <style
        dangerouslySetInnerHTML={{
          __html: `
            @import url('https://fonts.googleapis.com/css2?family=Baloo+2:wght@400;500;600;700;800&family=Plus+Jakarta+Sans:wght@200;300;400;500;600;700;800&display=swap');
            html, body {
              margin: 0;
              padding: 0;
              height: 100%;
              width: 100%;
              overflow-x: hidden;
            }
            #root {
              min-height: 100vh;
              display: flex;
              flex-direction: column;
            }
          `,
        }}
      />
    </div>
  )
}
