
import React, { useState } from 'react';
import { Box, Typography, Button, Container } from "@mui/material";
import GuestNavBar from "../components/GuestNavBar";
import AboutUsDog2 from "../assets/AboutUsDog2.png";
import SignUpModal from "../components/SignUpModal"; // Import SignUpModal
import { useLocation } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';


export default function AboutUs() {
  const colors = {
    yellow: "#F0B542",
    darkBlue: "#042C3C",
    coral: "#EA6C7B",
    cream: "#FFF7EC",
    green: "#8A9A5B",
  };

  const [isSignUpOpen, setIsSignUpOpen] = useState(false); // State for SignUpModal
  const navigate = useNavigate();
  
  return (
    <Box
      sx={{
        minHeight: "100vh",
        width: "100%",
        backgroundColor: colors.cream,
        position: "relative",
        overflowX: "auto",
        overflowY: "hidden",
        fontFamily: "'Plus Jakarta Sans', sans-serif",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <GuestNavBar colors={colors} />
      <Typography
        sx={{
          pt: 2,
          pl: 2,
          color: "text.secondary",
          fontSize: "16px",
        }}
      >
        About Us Page
      </Typography>
      <Box
        sx={{
          position: "absolute",
          top: "-100px",
          left: "-20px",
          width: "550px",
          height: "550px",
          backgroundColor: colors.yellow,
          borderRadius: "50%",
          transform: "scale(1.2)",
          zIndex: 0,
        }}
      />
      <Container
        maxWidth="lg"
        sx={{
          display: "flex",
          flexGrow: 1,
          position: "relative",
          zIndex: 5,
          padding: "0 20px",
        }}
      >
        <Box
          component="img"
          src={AboutUsDog2}
          alt="Bernese Mountain Dog"
          sx={{
            height: "auto",
            width: "80%",
            objectFit: "contain",
            objectPosition: "left center",
            marginTop: "-90px",
            marginLeft: "-350px"
          }}
        />
        <Box
          sx={{
            width: "65%",
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            alignItems: "center",
            padding: "0 20px",
          }}
        >
          <Typography
            variant="h1"
            sx={{
              fontSize: "100px",
              fontWeight: "bold",
              marginTop: "-180px",
              marginBottom: "20px",
              textAlign: "center",
              fontFamily: "'Baloo 2', cursive",
            }}
          >
            <Box component="span" sx={{ color: colors.green }}>
              STAY
            </Box>
            <Box component="span" sx={{ color: colors.darkBlue }}>
              {" "}
              on track.
            </Box>
          </Typography>
          <Typography
            variant="body1"
            sx={{
              fontSize: "16px",
              textAlign: "center",
              marginBottom: "30px",
              lineHeight: 1.4,
              color: colors.darkBlue,
            }}
          >
            Your pet's health, our priority! Track medications, monitor wellness, and explore expert care tips—all in
            one place. Join us and give your furry friend the love and care they deserve!
          </Typography>
          <Button
            variant="contained"
            sx={{
              backgroundColor: colors.coral,
              color: "white",
              borderRadius: "9999px",
              padding: "8px 32px",
              fontSize: "16px",
              textTransform: "none",
              boxShadow: "none",
              "&:hover": {
                backgroundColor: colors.coral,
                opacity: 0.9,
                boxShadow: "none",
              },
            }}
            onClick={() => navigate('/signup')}
            // Open SignUpModal directly
          >
            Sign Up
          </Button>
        </Box>
      </Container>
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

      {isSignUpOpen && (
        <SignUpModal
          onClose={() => setIsSignUpOpen(false)}
          onSignUp={(firstName, lastName, phoneNumber, email, password, confirmPassword) => {
            console.log("Sign up with:", firstName, lastName, phoneNumber, email, password);
            setIsSignUpOpen(false);
          }}
        />
      )}
    </Box>
  );
}